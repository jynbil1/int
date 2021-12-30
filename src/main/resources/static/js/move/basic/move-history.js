$(document).ready(function () {

   
    $(".delete-btn").on( "click", function() {
        var wh_move_seq = $(this).attr("data-id");
        var deleteRow = $(this).parent().parent();
        //console.log(md_order_id);

        var formData = {
            wh_move_seq: wh_move_seq
        }
    
        console.log(formData);
            $.ajax({
                url: '/deleteMoveProduct',
                type:'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success:function(response){
                    if (response.code == '200') {
                        $(deleteRow).addClass('hide');
                        alert("삭제성공");
                    } else if (response.code == '401'){
                        alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                    }else{
                        alert("서버오류 관리자에게 문의해주세요")
                    }
                }
            });// end getOneTransaction

    }); //end edit btn'


}); 