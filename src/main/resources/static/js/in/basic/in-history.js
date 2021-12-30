$(document).ready(function() {


    $(".delete-btn").on("click", function() {
        var wh_in_seq = $(this).attr("data-id");
        var deleteRow = $(this).parent().parent();
        //console.log(md_order_id);

        var formData = {
            wh_in_seq: wh_in_seq
        }

        console.log(formData);
        $.ajax({
            url: '/deleteInProduct',
            type: 'POST',
            data: JSON.stringify(formData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success: function(response) {
                if (response.code == '200') {
                    $(deleteRow).addClass('hide');
                    alert("삭제성공");
                } else if (response.code == '401') {
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                } else {
                    alert("서버오류 관리자에게 문의해주세요")
                }
            }
        }); // end getOneTransaction

    }); //end edit btn'

    // $('.selected-btn').click(function() {

    //     var wh_in_seq = $(this).parent().parent().find("[name='seq']").val();
    //     var wh_in_date = $(this).parent().parent().find("[name='date']").val();
    //     var product_name = $(this).parent().parent().find("[name='product-name']").val();
    //     var wh_in_qty = $(this).parent().parent().find("[name='qty']").val();
    //     var wh_in_location = $(this).parent().parent().find("[name='in-location']").val();
    //     var wh_pick_location = $(this).parent().parent().find("[name='pick-location']").val();
    //     var wh_in_expdate = $(this).parent().parent().find("[name='exp-date']").val();
    //     var wh_in_barcode = $(this).parent().parent().find("[name='barcode']").val();
    //     var create_user = $(this).parent().parent().find("[name='create-user']").val();
    //     console.log(wh_in_seq);
    //     console.log(wh_in_date);
    //     $('#wh_in_seq').val(wh_in_seq);
    //     $('#wh_in_date').val(wh_in_date);
    //     $('#product_name').val(product_name);
    //     $('#wh_in_qty').val(wh_in_qty);
    //     $('#wh_in_location').val(wh_in_location);
    //     $('#wh_pick_location').val(wh_pick_location);
    //     $('#wh_in_expdate').val(wh_in_expdate);
    //     $('#wh_in_barcode').val(wh_in_barcode);
    //     $('#create_user').val(create_user);

    //     $('#sendpost').attr("action", "/printInLabel");
    //     $('#sendpost').attr("method", "post");
    //     $('#sendpost').attr("target", "pdfpage" + wh_in_seq);
    //     window.open("", "pdfpage" + wh_in_seq, "");
    //     $('#sendpost').submit();

    // });


});