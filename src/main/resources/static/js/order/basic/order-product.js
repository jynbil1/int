$(document).ready(function() {
    $(window).on("load", function() {
        $(".loader-wrapper").fadeOut("slow");
    });

    var orderTable = $('#order-table').DataTable({
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    $('#order-table tbody ').on('click', 'tr', function() {
        $(this).toggleClass('selected');

    });


    

    $('#order-product').on('click', function(event) {
        event.preventDefault();
        var button = this;
        button.disabled = true;

        let selrows = 0;
        selrows = orderTable.rows('.selected').data().length;

        if (orderTable.rows('.selected').data().length < 1) {
            alert("선택된 줄이 없습니다. 입하할 줄을 선택해주세요.");
            button.disabled = false;
            return false;
        }

        var selectRowData = orderTable.rows('.selected').data();
        var list = [];
        for (var i = 0; i < selrows; i++) {

            var product_id = selectRowData[i][3];
            var pre_unit_price = "#unit-price" + product_id;
            var unit_price = $(pre_unit_price).val();

            var pre_shipping_fee = "#shipping-fee" + product_id;
            var shipping_fee = $(pre_shipping_fee).val();

            var pre_order_qty = "#order-qty" + product_id;
            var order_qty = $(pre_order_qty).val();

            var pre_order_price = "#order-price" + product_id;
            var order_price = $(pre_order_price).val();

            var product_name = selectRowData[i][4];
            
            var evidtypeID = "#evid-type" + product_id;
            var evid_type = $(evidtypeID).val();

            var content = {
              
                company_id: selectRowData[i][1],
                company_name: selectRowData[i][2],
                product_id: selectRowData[i][3],
                product_name: product_name,
                order_qty: order_qty,
                unit_price: unit_price,
                shipping_fee: shipping_fee,
                order_price: order_price,
                manager_id: selectRowData[i][13],
                manager_email: selectRowData[i][16],
                cc_email: selectRowData[i][18],
                hanpoom_PIC: selectRowData[i][19],
                pic_phone_number: selectRowData[i][20],
                evid_type: evid_type,
                is_taxable: selectRowData[i][21],
                transaction_req_date_type: selectRowData[i][22]

            };
            console.log(content)
            console.log("bugggggggggggggggggggggg")
            if (isNaN(order_price) == true ) {
                alert(product_name + "의 합계가 없습니다.");
                button.disabled = false;
                return false;
            } else {
                list.push(content);
            }
        }

        $("span.loading").css("display", "block");
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/orderProduct',
            data: JSON.stringify(list),
            dataType: "json",
            success: function(response) {
                var orderproduct = response.object;
                if (response.code == "200") {
                    $(".data-tables-empty").addClass('hide');
                    $("#order-table tbody tr").removeClass('selected');
                    button.disabled = false;
                    alert("작업 성공적으로 완료되었습니다.");
                    $("span.loading").css("display", "none");
                } else if(response.code == "500"){
                    alert(response.message);
                    $("span.loading").css("display", "none");
                }
                else {
                    alert("발주 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                    button.disabled = false;
                    $("span.loading").css("display", "none");
                }
            }
        });

    });

    $('#record-order-product').on('click', function(event) {
      event.preventDefault();
      var button = this;
      button.disabled = true;

      let selrows = 0;
      selrows = orderTable.rows('.selected').data().length;

      if (orderTable.rows('.selected').data().length < 1) {
          alert("선택된 줄이 없습니다. 입하할 줄을 선택해주세요.");
          button.disabled = false;
          return false;
      }

      var selectRowData = orderTable.rows('.selected').data();
      var list = [];
      for (var i = 0; i < selrows; i++) {

          var product_id = selectRowData[i][3];
          var pre_unit_price = "#unit-price" + product_id;
          var unit_price = $(pre_unit_price).val();

          var pre_shipping_fee = "#shipping-fee" + product_id;
          var shipping_fee = $(pre_shipping_fee).val();

          var pre_order_qty = "#order-qty" + product_id;
          var order_qty = $(pre_order_qty).val();

          var pre_order_price = "#order-price" + product_id;
          var order_price = $(pre_order_price).val();

          var product_name = selectRowData[i][4];

          var evidtypeID = "#evid-type" + product_id;
          var evid_type = $(evidtypeID).val();

          var orderDateID = "#order-date" + product_id;
            var orderDate = $(orderDateID).val();

          var content = {
              order_date : orderDate,
              company_id: selectRowData[i][1],
              company_name: selectRowData[i][2],
              product_id: selectRowData[i][3],
              product_name: selectRowData[i][4],
              order_qty: order_qty,
              unit_price: unit_price,
              shipping_fee: shipping_fee,
              order_price: order_price,
              manager_id: selectRowData[i][13],
              manager_email: selectRowData[i][16],
              cc_email: selectRowData[i][18],
              hanpoom_PIC: selectRowData[i][19],
              pic_phone_number: selectRowData[i][20],
              evid_type: evid_type,
              is_taxable: selectRowData[i][21],
              transaction_req_date_type: selectRowData[i][22]

          };
          console.log(content)
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          console.log("bugggggggggggggggggggggg")
          
          if (isNaN(order_price) == true) {
              alert(product_name + "의 합계가 없습니다.");
              button.disabled = false;
              return false;
          } else {
              list.push(content);
          }
      }
      console.log(list);
      $("span.loading").css("display", "block");

      $.ajax({
          type: 'POST',
          contentType: "application/json",
          url: '/recordOrderProduct',
          data: JSON.stringify(list),
          dataType: "json",
          success: function(response) {

              var orderproduct = response.object;
              
              if (response.code == "200") {

                  $(".data-tables-empty").addClass('hide');


                  $("#order-table tbody tr").removeClass('selected');

                  button.disabled = false;
                  alert("작업 성공적으로 완료되었습니다.");
                  $("span.loading").css("display", "none");
              } else {
                  alert("발주 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                  button.disabled = false;
                  $("span.loading").css("display", "none");
              }

          }
      });

  });

    $("#order-table").on("keyup"," input[name^=text]", function(){
      var $tr = $(this).closest('tr');
      var carton_unit = Number($tr.find('td:eq(6) input').val()); // 카톤단위
      var carton_quantity = Number($tr.find('td:eq(7) input').val()); // 카톤수량
      $tr.find('td:eq(8) input').val(carton_unit*carton_quantity);


      var total_quantity = Number($tr.find('td:eq(8) input').val()); // 총수량
      var supply_price = parseFloat($tr.find('td:eq(9) input').val()); // 공급가
      var shipping_fee = Number($tr.find('td:eq(10) input').val()); // 배송비
      var res2 = (total_quantity*supply_price)+shipping_fee;
      $tr.find('td:eq(11) input').val(total_quantity*supply_price+shipping_fee);    
  }); 

});

function sendCheckedIds() {
  var arr = [];
  $("input:checked[name=category").each(function() {
      arr.push($(this).val());
  });
}
function validateForm() {
  var a = document.getElementById("compname").value;
  var b = document.getElementById("prodname").value;
  
  var main = document.getElementById("maincat_check");
  var sub = document.getElementById("subcat_check");
  

  if ((a == null || a == "") && (b == null || b == "")) {
      return false;
  }
  else{
      return true;
  }   
}
