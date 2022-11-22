import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class Main {
    public  static final String URI = "https://api.nasa.gov/planetary/apod?api_key=26oonfxmwNMP4jwZt4F0BAgmBlCjnVvGMmZWvLR5";

    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        CloseableHttpResponse response = httpClient.execute(new HttpGet(URI));

        NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        System.out.println(nasaObject);

        // Отправляем запрос и получаем ответ с нашей картинкой
        CloseableHttpResponse pictureResponse = httpClient.execute(new HttpGet(nasaObject.getUrl()));

        //Формируем автоматически название для файла
        String[] arr = nasaObject.getUrl().split("/");
        String file = arr[arr.length - 1];

        //Проверяем что наш ответ не null
        HttpEntity entity = pictureResponse.getEntity();
        if (entity != null) {
            //сохраняем в файл
            FileOutputStream fos = new FileOutputStream(file);
            entity.writeTo(fos);
            fos.close();
        }
    }
}
