 $(document).ready(function() {

     var intable = $('#in-table').DataTable({
         "lengthMenu": [
             [10, 25, 50, -1],
             [10, 25, 50, "All"]
         ]
     });

     var intable2 = $('#in-table-2').DataTable({
         "lengthMenu": [
             [10, 25, 50, -1],
             [10, 25, 50, "All"]
         ]
     });

     var wh_in_seq_column = intable2.column(0);
     var wh_in_barcode_column = intable2.column(5);
     wh_in_seq_column.visible(!wh_in_seq_column.visible());
     wh_in_barcode_column.visible(!wh_in_barcode_column.visible());

     var cnt = 0;


     $('#in-table tbody ').on('click', 'tr', function() {
         $(this).toggleClass('selected');

     });


     $('#in-table-2 tbody').on('click', 'tr', function() {
         if ($(this).hasClass('selected')) {
             $(this).removeClass('selected');
         } else {
             intable2.$('tr.selected').removeClass('selected');
             $(this).addClass('selected');
         }
     });


     $('#in-product').on('click', function(event) {
         event.preventDefault();
         var button = this;
         button.disabled = true;


         var cnt2 = intable.rows('.selected').data().length;

         for (var i = 0; i < cnt2; i++) {
             if (intable.rows('.selected').data().length < 1) {
                 alert("선택된 상품이 없습니다. \n 입하할 상품을 선택해주세요.");
                 button.disabled = false;
                 return false;
             }
             // alert( table.rows('.selected').data().length +' row(s) selected' );

             var selectRowData = intable.rows('.selected').data();
             var arrival_seq = selectRowData[i][0];
             var product_id = selectRowData[i][1];
             var product_name = selectRowData[i][2];
             var qty = parseInt(selectRowData[i][3].replace(',', ''));
             var expdate = selectRowData[i][4];
             var inputLocID = "#input-loc" + arrival_seq;
             var inputQtyID = "#input-qty" + arrival_seq;
             var inputLocValue = $(inputLocID).val();
             var inputQtyValue = $(inputQtyID).val();
             console.log(qty);
             console.log(inputQtyValue);
             console.log(inputQtyValue > qty);
             if (inputQtyValue <= 0) {
                 alert(product_name + "의 입고 수량이 없습니다.");
                 button.disabled = false;
                 return false;
             }
             if (inputQtyValue > qty) {
                 alert(product_name + "의 입고 수량이 현재 수량보다 많습니다.");
                 console.log(qty);
                 console.log(inputQtyValue);
                 button.disabled = false;
                 return false;
             }
             if (inputLocValue == '') {
                 alert(product_name + "의 입고 위치가 없습니다.");
                 button.disabled = false;
                 return false;
             }

             var content = {

                 wh_arrival_seq: arrival_seq,
                 wh_in_productid: product_id,
                 product_name: selectRowData[i][2],
                 wh_in_location: inputLocValue,
                 wh_in_barcode: selectRowData[i][5],
                 wh_in_qty: inputQtyValue,
                 wh_in_expdate: expdate

             };
             console.log(content);
             $.ajax({
                 type: 'POST',
                 contentType: "application/json",
                 url: '/inProduct',
                 data: JSON.stringify(content),
                 dataType: "json",
                 success: function(response) {

                     var enterproduct = response.object;

                     if (response.code == "200") {

                         $(".data-tables-empty").addClass('hide');
                         var wh_in_seq = enterproduct.wh_in_seq;
                         var product_id = enterproduct.wh_in_productid;
                         var product_name = enterproduct.product_name;
                         var location = enterproduct.wh_in_location;
                         var qty = enterproduct.wh_in_qty;
                         var date;
                         var exp_date = null;

                         if (enterproduct.wh_in_expdate) {
                             date = new Date(enterproduct.wh_in_expdate);
                             exp_date = date.getFullYear() + ". " + (date.getMonth() + 1) + ". " + date.getDate();
                         }

                         var barcode = enterproduct.wh_in_barcode;
                         var excutor = enterproduct.create_user;
                         var excution_time = new Date(enterproduct.create_time);

                         console.log(product_name);
                         intable2.row.add([wh_in_seq, product_name, location, qty, exp_date, barcode, excutor, excution_time]).draw(false);
                         if (wh_in_seq_column.visible()) {
                             wh_in_seq_column.visible(!wh_in_seq_column.visible());
                         }
                         if (wh_in_barcode_column.visible()) {
                             wh_in_barcode_column.visible(!wh_in_barcode_column.visible());
                         }

                         $("#in-table tbody tr").removeClass('selected');

                         alert(product_name + " " +
                             String(qty) + " 개가 \n" +
                             location + " 위치에" +
                             "입고되었습니다.");
                         button.disabled = false;

                     } else {
                         alert("입고실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                         button.disabled = false;
                     }

                 }
             });
         }



     });

     $('#delete-in-product').on('click', function(event) {
         event.preventDefault();
         var button = this;
         button.disabled = true;


         var selectRowData = intable2.rows('.selected').data();

         if (intable2.rows('.selected').data().length < 1) {
             alert("선택된 Today 입고 목록이 없습니다.");
             return false;
         }

         var content = {

             wh_in_seq: selectRowData[0][0]

         };

         $.ajax({
             type: 'POST',
             contentType: "application/json",
             url: '/deleteInProduct',
             data: JSON.stringify(content),
             dataType: "json",
             success: function(response) {

                 if (response.code == "200") {

                     alert("삭제성공");

                     intable2.rows('.selected').remove().draw();

                     var cntData = intable2.rows().data().length;

                     if (cntData == 0) {
                         console.log("success");
                         $(".data-tables-empty").addClass('show');
                     } else {
                         $(".data-tables-empty").addClass('hide');
                     }
                     button.disabled = false;

                 } else {
                     alert("삭제실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                 }

             }
         });

     });

     // 입고pdf출력

     $('.selected-btn').on('click', function(event) {
         $('.item-rows').removeClass('selected');
         $(event.target).closest(".item-rows").addClass("selected");
     });

     $('.selected-btn').click(function() {
         var selectRowData = intable2.rows('.selected').data();
        console.log(selectRowData)
         $('#wh_in_seq').val(selectRowData[0][0]);
         $('#wh_in_date').val(selectRowData[0][1]);
         $('#product_name').val(selectRowData[0][2]);
         $('#wh_in_qty').val(selectRowData[0][3]);
         $('#wh_in_location').val(selectRowData[0][4]);
         $('#wh_pick_location').val(selectRowData[0][5]);
         $('#wh_in_expdate').val(selectRowData[0][6]);
         $('#wh_in_barcode').val(selectRowData[0][7]);
         $('#create_user').val(selectRowData[0][8]);

         $('#sendpost').attr("action", "/printInLabel");
         $('#sendpost').attr("method", "post");
         $('#sendpost').attr("target", "pdfpage" + selectRowData[0][0]);
         window.open("", "pdfpage" + selectRowData[0][0], "");
         $('#sendpost').submit();

     });

     $(window).on("load", function() {
         $(".loader-wrapper").fadeOut("slow");
     });
 });