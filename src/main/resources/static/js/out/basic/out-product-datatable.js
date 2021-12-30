$(document).ready(function() {

    var outTable = $('#out-table').DataTable({
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });
    var operationTable = $('#operation-table').DataTable({
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    $(".search-btn").on( "click", function() {

        var product_id = $('#product-id').val();
        
        //console.log(md_order_id);

        var formData = {
              product_id : product_id

        }
    
        console.log(formData);
            $.ajax({
                url: '/quantityByStatus',
                type:'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success:function(response){
                    var product = response.object;
                    var tr = '';
                    if (response.code == '200') {
                        tr += '<tr><td>'
                        + product.product_id + '</td><td>'
                        + product.product_name + '</td><td>'
                        + product.stocking_stock + '</td><td>'
                        + product.operation_stock + '</td><td>'
                        + product.cur_stock + '</td><td>'
                        + product.site_qty + '</td><td>'
                        + product.arrival_qty + '</td><td>'
                        + product.enter_qty + '</td><td>'
                        + product.move_qty + '</td><td>'
                        + product.loss_qty + '</td><td>'
                        + product.processing_qty + '</td><td>'
                        + product.preparing_qty + '</td><td>'
                        + product.processing_event_qty + '</td><td>'
                        + product.out_qty 
                        + '</td></tr>';
                        $('tbody#product').html(tr);
                        alert("검색성공");
                    } else if (response.code == '401'){
                        alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                    }else{
                        alert("서버오류 관리자에게 문의해주세요")
                    }
                }
            });// end getOneTransaction

    }); //end edit btn'

    $(window).on("load", function() {
        $(".loader-wrapper").fadeOut("slow");
    });
    // document.getElementById('search-date').valueAsDate = new Date();


});