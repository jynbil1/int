$(document).ready(function() {
    
    var arrivaltable = $('#expdate-table').DataTable({
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    });

    var arrivaltable2 = $('#expdate-table-2').DataTable({
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    });

    // var arrivaltable3 = $('#arrivalHistoryTable').DataTable({
    //     "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    // });

    

    //today inlist
    var wh_arrival_seq_column = arrivaltable2.column(0);
    var wh_arrival_barcode_column = arrivaltable2.column(4);
    wh_arrival_seq_column.visible(! wh_arrival_seq_column.visible());
    wh_arrival_barcode_column.visible(! wh_arrival_barcode_column.visible());
    
    var cnt = 0;

  
    $('#expdate-table tbody ').on( 'click', 'tr', function () {
        $(this).toggleClass('selected'); 
    });
   
    
    $('#expdate-table-2 tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
            arrivaltable2.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    } );

    // var button;
    $('#arrivalProduct').on('click', function (event) {
        event.preventDefault();
        // $(this).prop('disabled', true);
        var button = this;
        button.disabled = true;
        
        var cnt2 =arrivaltable.rows('.selected').data().length;

        if(arrivaltable.rows('.selected').data().length < 1){
            alert("선택된 줄이 없습니다. 입하할 줄을 선택해주세요.");
            button.disabled = false;
            return false;
        }
        
        for (var i = 0; i < cnt2; i++){
 

            // alert( table.rows('.selected').data().length +' row(s) selected' );
            
            var selectRowData = arrivaltable.rows('.selected').data();
            var product_id = selectRowData[i][0];
            var product_name = selectRowData[i][1];
            var inputQtyID = "#inputqty"+ product_id;
            var inputDateID = "#inputdate"+ product_id;
            var inputQtyValue = $(inputQtyID).val();
            var inputDateValue = $(inputDateID).val();

            if(inputQtyValue <= 0){
                alert(product_name + "의 입하수량을 입력해주세요");
                button.disabled = false;
                return false;
            }
            if(inputDateValue == ''){
                alert(product_name + "의 유효기간이 입력되지 않았습니다. \n 유효기간이 없는 상품이면 무시해도 됩니다.");
            }

            var content = {
                
                wh_arrival_productid : product_id,
                product_name : selectRowData[i][1],
                wh_arrival_barcode : selectRowData[i][4],
                wh_arrival_qty : inputQtyValue,
                wh_arrival_expdate : inputDateValue

            };

            $.ajax({
                type: 'POST',
                contentType : "application/json",
                url: '/arrivalExpdateProduct',
                data: JSON.stringify(content),
                dataType: "json",
                success: function(response){

                    var arrivalproduct = response.object;

                    if(response.code == "200"){

                        $(".dataTables_empty").addClass('hide');
                        var wh_arrival_seq = arrivalproduct.wh_arrival_seq;
                        var product_id = arrivalproduct.wh_arrival_productid;
                        var product_name = arrivalproduct.product_name;
                        var qty = arrivalproduct.wh_arrival_qty;
                        var date;
                        var exp_date = null;

                        if(arrivalproduct.wh_arrival_expdate){
                            date = new Date(arrivalproduct.wh_arrival_expdate);
                            exp_date = date.getFullYear() + ". " + (date.getMonth()+1)  + ". "+ date.getDate();
                        }
                        
                        var barcode = arrivalproduct.wh_arrival_barcode;
                        var excutor = arrivalproduct.create_user;
                        var excution_time = new Date(arrivalproduct.create_time);

                        console.log(product_name);
                        arrivaltable2.row.add([wh_arrival_seq, product_name, qty, exp_date, barcode, excutor,excution_time]).draw(false);
                        if(wh_arrival_seq_column.visible()){
                            wh_arrival_seq_column.visible(! wh_arrival_seq_column.visible());
                        }
                        if(wh_arrival_barcode_column.visible()){
                            wh_arrival_barcode_column.visible(! wh_arrival_barcode_column.visible());
                        }

                        $("#expdate-table tbody tr").removeClass('selected');

                        alert(product_name + " " +
                        String(qty) + " 개가 \n" +
                        "입하처리 되었습니다.");
                        button.disabled = false;
                    }else {
                        alert("입하 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                        button.disabled = false;
                    }
        
                }
            });
        }
       
        // location.reload();
        

    } );
    
    $('#deleteArrivalProduct').on('click', function (event) {
        event.preventDefault();
        var button = this;
        button.disabled = true;


        if(arrivaltable2.rows('.selected').data().length < 1){
            alert("선택된 줄이 없습니다. 입고취소할 줄을 선택해주세요.");
            button.disabled = false;
            return false;
        }
        
        var selectRowData = arrivaltable2.rows('.selected').data();

        var content = {
            
            wh_arrival_seq : selectRowData[0][0]

        };

        $.ajax({
            type: 'POST',
            contentType : "application/json",
            url: '/deleteArrivalProduct',
            data: JSON.stringify(content),
            dataType: "json",
            success: function(response){
                
                if(response.code == "200"){
                    
                    alert("입하삭제 성공");

                    arrivaltable2.rows('.selected').remove().draw();

                    var cntData = arrivaltable2.rows().data().length;

                    if(cntData == 0){
                        console.log("success");
                        $(".dataTables_empty").addClass('show');   
                    }else{
                        $(".dataTables_empty").addClass('hide');
                    }

                    button.disabled = false;

                }else {
                    alert("입하삭제 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                    button.disabled = false;
                }
    
             }
         }); 

    } );
    
    // button.disabled = false;
    $(window).on("load",function(){
        $(".loader-wrapper").fadeOut("slow");
    });

    

});
