$(document).ready(function () {

   
    $(".move-btn").on( "click", function() {
        var wh_in_seq = $(this).attr("data-id");
        var deleteRow = $(this).parent().parent();
        var arrival_seq = $(this).parent().parent().find("[name='arrival-seq']").val();
        var product_id = $(this).parent().parent().find("[name='product-id']").val();
        var existing_in_loc = $(this).parent().parent().find("[name='existing-in-loc']").val();
        var moving_in_loc = $(this).parent().parent().find("[name='moving-in-loc']").val();
        var existing_in_expdate = $(this).parent().parent().find("[name='existing-in-expdate']").val();
        var moving_in_expdate = $(this).parent().parent().find("[name='moving-in-expdate']").val();
        var existing_in_qty = $(this).parent().parent().find("[name='existing-in-qty']").val();
        var move_qty = $(this).parent().parent().find("[name='move-qty']").val();
        
        //console.log(md_order_id);

        var formData = {
              wh_in_seq : wh_in_seq
            , wh_arrival_seq : arrival_seq
            , wh_in_productid : product_id
            , existing_in_loc : existing_in_loc
            , existing_in_expdate : existing_in_expdate
            , moving_in_expdate : moving_in_expdate
            , moving_in_loc : moving_in_loc
            , existing_in_qty : existing_in_qty
            , move_qty : move_qty
        }
    
        console.log(formData);
            $.ajax({
                url: '/stockingMove',
                type:'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success:function(response){
                    if (response.code == '200') {
                        alert("이동성공");
                    } else if (response.code == '401'){
                        alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                    }else{
                        alert("서버오류 관리자에게 문의해주세요")
                    }
                }
            });// end getOneTransaction

    }); //end edit btn'


}); 