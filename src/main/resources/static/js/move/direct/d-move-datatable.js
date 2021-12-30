 $(document).ready(function() {

     var dmovetable = $('#d-move-table').DataTable({

     });

     var dmovetable2 = $('#d-move-table-2').DataTable({

     });

     var wh_dmove_seq_column = dmovetable2.column(0);
     var wh_dmove_barcode_column = dmovetable2.column(5);
     wh_dmove_seq_column.visible(!wh_dmove_seq_column.visible());
     wh_dmove_barcode_column.visible(!wh_dmove_barcode_column.visible());

     $('#d-move-table tbody ').on('click', 'tr', function() {
         $(this).toggleClass('selected');

     });

     $('#d-move-table-2 tbody').on('click', 'tr', function() {
         if ($(this).hasClass('selected')) {
             $(this).removeClass('selected');
         } else {
             dmovetable2.$('tr.selected').removeClass('selected');
             $(this).addClass('selected');
         }
     });

     $('#d-move-product').on('click', function(event) {
         event.preventDefault();
         var button = this;
         button.disabled = true;


         var cnt2 = dmovetable.rows('.selected').data().length;

         for (var i = 0; i < cnt2; i++) {
             if (dmovetable.rows('.selected').data().length < 1) {
                 alert("선택된 상품이 없습니다. \n 입하할 상품을 선택해주세요.");
                 button.disabled = false;
                 return false;
             }
             // alert( table.rows('.selected').data().length +' row(s) selected' );

             var selectRowData = dmovetable.rows('.selected').data();
             var arrival_seq = selectRowData[i][0];
             var product_id = selectRowData[i][1];
             var product_name = selectRowData[i][2];
             var qty = parseInt(selectRowData[i][3].replace(',', ''));
             var expdate = selectRowData[i][4];
             var inputLocID = "#input-loc" + arrival_seq;
             var inputQtyID = "#input-qty" + arrival_seq;
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
                 alert(product_name + "의 이동 위치(운영동)가 없습니다.");
                 button.disabled = false;
                 return false;
             }

             var content = {

                 wh_arrival_seq: arrival_seq,
                 wh_in_productid: product_id,
                 product_name: product_name,
                 wh_pick_location: inputLocValue,
                 wh_move_expdate: expdate,
                 wh_move_qty: inputQtyValue

             };
             console.log(content);
             $.ajax({
                 type: 'POST',
                 contentType: "application/json",
                 url: '/dMoveProduct',
                 data: JSON.stringify(content),
                 dataType: "json",
                 success: function(response) {

                     var dmoveproduct = response.object;

                     if (response.code == "200") {

                         $(".data-tables-empty").addClass('hide');
                         var wh_move_seq = dmoveproduct.wh_move_seq;
                         var product_id = dmoveproduct.wh_in_productid;
                         var product_name = dmoveproduct.product_name;
                         var in_location = dmoveproduct.wh_in_location;
                         var pick_location = dmoveproduct.wh_pick_location;
                         var qty = dmoveproduct.wh_move_qty;
                         var date;
                         var exp_date = null;

                         if (dmoveproduct.wh_move_expdate) {
                             date = new Date(dmoveproduct.wh_move_expdate);
                             exp_date = date.getFullYear() + ". " + (date.getMonth() + 1) + ". " + date.getDate();
                         }

                         var barcode = dmoveproduct.wh_move_barcode;
                         var excutor = dmoveproduct.create_user;
                         var excution_time = new Date(dmoveproduct.create_time);


                         dmovetable2.row.add([wh_move_seq, product_name, pick_location, qty, exp_date, barcode, excutor, excution_time]).draw(false);
                         if (wh_dmove_seq_column.visible()) {
                             wh_dmove_seq_column.visible(!wh_dmove_seq_column.visible());
                         }

                         if (wh_dmove_barcode_column.visible()) {
                             wh_dmove_barcode_column.visible(!wh_dmove_barcode_column.visible());
                         }

                         $("#d-move-table tbody tr").removeClass('selected');

                         alert(product_name + " " +
                             String(qty) + " 개가 \n" +
                             pick_location + "위치로 " +
                             "\n 바로 이동처리 되었습니다.");
                         button.disabled = false;

                     }

                 }
             });
         }

     });

     $('#delete-d-move-product').click(function() {
         if (dmovetable2.rows('.selected').data().length < 1) {
             alert("선택된 줄이 없습니다. 이동취소할 줄을 선택해주세요.");
             return false;
         }

         var selectRowData = dmovetable2.rows('.selected').data();

         var content = {

             wh_move_seq: selectRowData[0][0]

         };

         $.ajax({
             type: 'POST',
             contentType: "application/json",
             url: '/deleteDMoveProduct',
             data: JSON.stringify(content),
             dataType: "json",
             success: function(response) {

                 if (response.code == "200") {
                     alert("물품 이동 내역 삭제 성공");
                     dmovetable2.rows('.selected').remove().draw();

                     var cntData = dmovetable2.rows().data().length;

                     if (cntData == 0) {
                         $(".data-tables-empty").addClass('show');
                     } else {
                         $(".data-tables-empty").addClass('hide');
                     }


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