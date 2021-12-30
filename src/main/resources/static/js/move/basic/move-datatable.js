 $(document).ready(function() {

     var movetable = $('#move-table').DataTable({
         "lengthMenu": [
             [10, 25, 50, -1],
             [10, 25, 50, "All"]
         ]
     });

     var movetable2 = $('#move-table-2').DataTable({
         "lengthMenu": [
             [10, 25, 50, -1],
             [10, 25, 50, "All"]
         ]
     });

     var wh_move_seq_column = movetable2.column(0);
     var wh_move_barcode_column = movetable2.column(6);
     wh_move_seq_column.visible(!wh_move_seq_column.visible());
     wh_move_barcode_column.visible(!wh_move_barcode_column.visible());

     $('#move-table tbody ').on('click', 'tr', function() {
         $(this).toggleClass('selected');

     });

     $('#move-table-2 tbody').on('click', 'tr', function() {
         if ($(this).hasClass('selected')) {
             $(this).removeClass('selected');
         } else {
             movetable2.$('tr.selected').removeClass('selected');
             $(this).addClass('selected');
         }
     });

     $('#move-product').on('click', function(event) {
         event.preventDefault();
         var button = this;
         button.disabled = true;


         var cnt2 = movetable.rows('.selected').data().length;

         if (movetable.rows('.selected').data().length < 1) {
             alert("선택된 줄이 없습니다. 이동할 줄을 선택해주세요.");
             button.disabled = false;
             return false;
         }

         for (var i = 0; i < cnt2; i++) {


             // alert( table.rows('.selected').data().length +' row(s) selected' );

             var selectRowData = movetable.rows('.selected').data();
             var seq = selectRowData[i][0];
             var product_name = selectRowData[i][1];
             var qty = parseInt(selectRowData[i][5].replace(',', ''));
             var inputLocID = "#input-loc" + seq;
             var inputQtyID = "#input-qty" + seq;
             var inputLocValue = $(inputLocID).val();
             var inputQtyValue = $(inputQtyID).val();

             if (inputQtyValue == 0) {
                 alert("이동수량을 입력해주세요");
                 button.disabled = false;
                 return false;
             } else if (inputQtyValue < 0) {
                 alert("이동수량을 양수로 입력해주세요");
                 button.disabled = false;
                 return false;
             } else if (inputQtyValue > qty) {
                 alert(product_name + "의 이동 수량이 현재 수량보다 많습니다.");
                 button.disabled = false;
                 return false;
             }
             if (inputLocValue == '') {
                 alert("운영동위치가 입력되지 않았습니다.");
                 button.disabled = false;
                 return false;
             }

             var content = {

                 wh_in_productid: selectRowData[i][1],
                 product_name: product_name,
                 wh_in_location: selectRowData[i][3],
                 wh_pick_location: inputLocValue,
                 wh_move_expdate: selectRowData[i][4],
                 wh_move_barcode: selectRowData[i][6],
                 wh_move_qty: inputQtyValue,
                 wh_in_seq : seq

             };

             $.ajax({
                 type: 'POST',
                 contentType: "application/json",
                 url: '/moveProduct',
                 data: JSON.stringify(content),
                 dataType: "json",
                 success: function(response) {

                     var moveproduct = response.object;

                     if (response.code == "200") {

                         $(".data-tables-empty").addClass('hide');
                         var wh_move_seq = moveproduct.wh_move_seq;
                         var product_id = moveproduct.wh_in_productid;
                         var product_name = moveproduct.product_name;
                         var in_location = moveproduct.wh_in_location;
                         var pick_location = moveproduct.wh_pick_location;
                         var qty = moveproduct.wh_move_qty;
                         var date;
                         var exp_date = null;

                         if (moveproduct.wh_move_expdate) {
                             date = new Date(moveproduct.wh_move_expdate);
                             exp_date = date.getFullYear() + ". " + (date.getMonth() + 1) + ". " + date.getDate();
                         }

                         var barcode = moveproduct.wh_move_barcode;
                         var excutor = moveproduct.create_user;
                         var excution_time = new Date(moveproduct.create_time);

                         movetable2.row.add([wh_move_seq, product_name, in_location, pick_location, qty, exp_date, barcode, excutor, excution_time]).draw(false);
                         if (wh_move_seq_column.visible()) {
                             wh_move_seq_column.visible(!wh_move_seq_column.visible());
                         }
                         if (wh_move_barcode_column.visible()) {
                             wh_move_barcode_column.visible(!wh_move_barcode_column.visible());
                         }

                         $("#move-table tbody tr").removeClass('selected');

                         alert(product_name + " " +
                             String(qty) + " 개가 \n" +
                             in_location + "위치에서 " +
                             pick_location + "위치에서 " +
                             "\n 이동처리 되었습니다.");
                         button.disabled = false;

                     }

                 }
             });
         }


     });

     $('#delete-move-product').on('click', function(event) {
         event.preventDefault();
         var button = this;
         button.disabled = true;


         if (movetable2.rows('.selected').data().length < 1) {
             alert("선택된 줄이 없습니다. 이동취소할 줄을 선택해주세요.");
             return false;
         }

         var selectRowData = movetable2.rows('.selected').data();

         var content = {

             wh_move_seq: selectRowData[0][0]

         };

         $.ajax({
             type: 'POST',
             contentType: "application/json",
             url: '/deleteMoveProduct',
             data: JSON.stringify(content),
             dataType: "json",
             success: function(response) {

                 if (response.code == "200") {
                     alert("물품 이동 내역 삭제 성공");
                     movetable2.rows('.selected').remove().draw();

                     var cntData = movetable2.rows().data().length;

                     if (cntData == 0) {
                         $(".data-tables-empty").addClass('show');
                     } else {
                         $(".data-tables-empty").addClass('hide');
                     }
                     button.disabled = false;

                 } else {
                     alert("물품 이동 내역 삭제 실패");
                 }

             }
         });

     });

     $(window).on("load", function() {
         $(".loader-wrapper").fadeOut("slow");
     });

 });