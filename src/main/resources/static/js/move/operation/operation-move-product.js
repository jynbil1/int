$(document).ready(function () {

   
    $(".move-btn").on( "click", function() {
        var product_id = $(this).attr("data-id");
        var moving_pick_loc = $(this).parent().parent().find("[name='moving-pick-loc']").val();

        
        //console.log(md_order_id);

        var formData = {

             wh_in_productid : product_id
            , wh_pick_location : moving_pick_loc
        }
    
        console.log(formData);
            $.ajax({
                url: '/operationMove',
                type:'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success:function(response){
                    if (response.code == '200') {
                        alert("위치변경 완료");
                    } else if (response.code == '401'){
                        alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                    }else{
                        alert("서버오류 관리자에게 문의해주세요")
                    }
                }
            });// end getOneTransaction

    }); //end edit btn'


}); 