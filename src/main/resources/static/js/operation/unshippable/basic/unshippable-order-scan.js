var redirect_;
var summary_details;
var isValidUser;

$(document).ready(function () {

    $('#orderNoSubmit').on("click", function (event) {
        
        var i_order_no = $("#order-no").val();
        $('#order_no').val(i_order_no)
        console.log(i_order_no);
        callValidateCheckingOrderNo(i_order_no);
    });
    
    $('#orderNoSubmit').hide();

    $('#order-no').keypress(function (e) {
        var key = e.which;

        //if keypass 'Enter'
        if (key == 13) {
            $('#orderNoSubmit').click();
        }
    });
    
});

function callValidateCheckingOrderNo(order_no) {
    $.ajax({
        url: '/operation/checkOrderNo?order_no=' + order_no,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var code = response.code;
            var message = response.message;
            console.log(response)
            if(code == "200"){
                console.log("success")
                $('#unshippable').attr("action","/operation/order-unshippable");
                $('#unshippable').attr("method","POST");
                $('#unshippable').submit();    
            }else{
                alert(message + " 상태인 주문건이여서 미출고 처리할 수 없습니다.");
            }

            
        },
        error: function () {
            console.log("error");
        }
    });
}
