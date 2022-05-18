package hanpoom.internal_cron.crons.dashboard.common.vo;

public class ErrorResponse extends APIResponse {
    public ErrorResponse() {
        this.statusCode = 400;
        this.message = "데이터를 처리할 수 없습니다.";
    }

    public ErrorResponse(int statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.message = errorMessage;
    }
}
