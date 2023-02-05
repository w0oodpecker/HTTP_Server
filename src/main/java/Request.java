import org.apache.http.NameValuePair;

import java.util.List;

public class Request {

    private String method;
    private String path;
    private String version;
    private List<NameValuePair> queryParams;


    Request(String method, String path, String version, List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParams = queryParams;
    }


    Request(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParams = null;
    }


    public String getMethod() {
        return method;
    }


    public String getPath() {
        return path;
    }


    public List<NameValuePair> getQueryParams() { //Возвращает список пар имен параметров и их значений
        return this.queryParams;
    }


    public String getQueryParam(String name) { //Возврат значения по имени параматра
        for (int i = 0; i < this.queryParams.size(); i++) {
            if (this.queryParams.get(i).getName().equals(name)) {
                return this.queryParams.get(i).getName();
            }
        }
        return null;
    }


}
