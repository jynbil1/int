package hanpoom.internal_cron.crons.dashboard.common.vo;

public class SuccessResponse extends APIResponse {
    public SuccessResponse() {
        this.statusCode = 200;
        this.message = "success";
    }

    public SuccessResponse(int statusCode, String message, String strObject) {
        this.statusCode = statusCode;
        this.message = message;
        this.strObject = strObject;
    }

    public SuccessResponse(int statusCode, String message, Object object) {
        this.statusCode = statusCode;
        this.message = message;
        this.object = object;
    }

    public SuccessResponse(int statusCode, String strObject) {
        this.statusCode = statusCode;
        this.message = "success";
        this.strObject = strObject;
    }

    public SuccessResponse(int statusCode, Object object) {
        this.statusCode = statusCode;
        this.message = "success";
        this.object = object;
    }

    public SuccessResponse(String strObject) {
        this.statusCode = 200;
        this.message = "success";
        this.strObject = strObject;
    }

    public SuccessResponse(Object object) {
        this.statusCode = 200;
        this.message = "success";
        this.object = object;
    }
}
