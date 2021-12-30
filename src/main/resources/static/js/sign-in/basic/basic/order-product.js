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




      $('#order-prod-func').on('click', function(event) {
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

              var pre1_unit_price = selectRowData[i][2];
              var pre_unit_price = "#text-price" + pre1_unit_price;
              var unit_price = $(pre_unit_price).val();

              var pre1_shipping_fee = selectRowData[i][2];
              var pre_shipping_fee = "#text-shipping" + pre1_shipping_fee;
              var shipping_fee = $(pre_shipping_fee).val();

              var pre1_order_qty = selectRowData[i][2];
              var pre_order_qty = "#result-1" + pre1_order_qty;
              var order_qty = $(pre_order_qty).val();

              var pre1_order_price = selectRowData[i][2];
              var pre_order_price = "#result-2" + pre1_order_price;
              var order_price = $(pre_order_price).val();


              // var content = {             
              //   company_id : selectRowData[i][0],
              //   product_id : selectRowData[i][2],
              //   order_qty : sum,
              //   unit_price : price,
              //   shipping_fee : ship,
              //   manager_id : selectRowData[i][11]
              // };
              var content = {
                  company_id: selectRowData[i][0],
                  company_name: selectRowData[i][1],
                  product_id: selectRowData[i][2],
                  product_name: selectRowData[i][3],
                  order_qty: order_qty,
                  unit_price: unit_price,
                  shipping_fee: shipping_fee,
                  order_price: order_price,
                  manager_id: selectRowData[i][11],
                  manager_email: selectRowData[i][14],
                  cc_email: selectRowData[i][16]

              };
              if (isNaN(order_price) == true || order_price == 0) {
                  alert(product_name + "의 합계가 0입니다.");
                  button.disabled = false;
                  return false;
              } else {
                  list.push(content);
              }
          }


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
                      //   <#--  alert(product_name + " " +
                      //   String(qty) + " 개가 \n" +
                      //   "입하처리 되었습니다.")  -->
                      button.disabled = false;
                      alert("작업 성공적으로 완료되었습니다.");

                  } else {
                      alert("발주 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
                      button.disabled = false;
                  }

              }
          });

      });

  });

  function sendCheckedIds() {
      var arr = [];
      $("input:checked[name=category").each(function() {
          arr.push($(this).val());
      });

      // $.ajax({
      //   type: "post",
      //   data: {category:arr},
      //   url: "/searchOrderProduct",
      //   success: function(data) {
      //     $('#response').html(data);
      //   }
      // });
  }