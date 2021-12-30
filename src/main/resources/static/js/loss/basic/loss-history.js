$(document).ready(function () {

   
    $(".delete-btn").on( "click", function() {
        var wh_loss_seq = $(this).attr("data-id");
        var deleteRow = $(this).parent().parent();
        var product_id = $(this).parent().parent().find("[name='product-id']").val();
        var qty = $(this).parent().parent().find("[name='qty']").val();
        //console.log(md_order_id);
       
        var loss_content = {
            wh_loss_seq: wh_loss_seq,
            wh_loss_productid : product_id,
            wh_loss_qty : qty
            
        }
    
        console.log(loss_content);
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/cancelLossProductRecord',
            data: JSON.stringify(loss_content),
            dataType: "json",
            success: function(response) {
                console.log(response);
                if (response.code == '200') {
                    $(deleteRow).addClass('hide');
                    alert("복구 성공");
                    
                } else if (response.code == '403') {
                    // If login Session is Expired,
                    window.location.href = "/lossProductHistory";

                } else {
                    alert("데이터 입력 오류");
                    
                }

            }
        });// end cancelLossProductRecord

    }); //end edit btn'



}); 