  
$(document).ready(function() {
  $(window).on("load",function(){
     $(".loader-wrapper").fadeOut("slow");
   });
 
   var orderTable = $('#lowest-price-order-table').DataTable({
       "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
     });
 
     $('#lowest-price-order-table tbody ').on( 'click', 'tr', function () {
         $(this).toggleClass('selected');
         
     } );
     
   
 
 
   $('#order-product').on('click', function (event){
     event.preventDefault();
     var button = this;
     button.disabled = true;
   
     let selrows = 0;
     selrows = orderTable.rows('.selected').data().length;
 
     if(orderTable.rows('.selected').data().length < 1){
         alert("선택된 줄이 없습니다. 발주할 줄을 선택해주세요.");
         button.disabled = false;
         return false;
     }
 
     var selectRowData = orderTable.rows('.selected').data();
     var list = [];
     for (var i = 0; i < selrows; i++){
 
       var product_id = selectRowData[i][0];
       var product_name = selectRowData[i][1];
       var unit_price_id = "#unitprice"+product_id;
       var unit_price = $(unit_price_id).val();
 
       var shipping_fee_id = "#shippingfee"+product_id;
       var shipping_fee = $(shipping_fee_id).val();
 
       var order_qty_id = "#orderqty"+product_id;
       var order_qty = $(order_qty_id).val();
 
       var order_price_id = "#orderprice"+product_id;
       var order_price = $(order_price_id).val();
 
       var company_select_id = "#companyname"+product_id;
       var company_select_value = $(company_select_id).val();
 
       var evidtypeID = "#evid-type" + product_id;
       var evid_type = $(evidtypeID).val();
 
       var orderDateID = "#order-date" + product_id;
       var orderDate = $(orderDateID).val();
       // console.log(company_select_id);
       // console.log(company_select_value);
       
       // var content = {             
       //   company_id : selectRowData[i][0],
       //   product_id : selectRowData[i][2],
       //   order_qty : sum,
       //   unit_price : price,
       //   shipping_fee : ship,
       //   manager_id : selectRowData[i][11]
       // };
       var content = {     
         order_date : orderDate,        
         company_id : company_select_value,
         product_id : product_id,
         product_name : product_name,
         order_qty : order_qty,
         unit_price : unit_price,
         shipping_fee : shipping_fee,
         order_price : order_price,
         evid_type : evid_type,
         is_taxable: selectRowData[i][12],
         transaction_req_date_type: selectRowData[i][13]
 
       };
       if(isNaN(order_price) == true || order_price == 0){
         alert(product_name + "의 합계가 0입니다.");
         button.disabled = false;
         return false;
       }else{
         list.push(content);
       } 
     }
       console.log(list)
     
     
       $.ajax({
         type: 'POST',
         contentType : "application/json",
         url: '/lowestpriceOrder',
         data: JSON.stringify(list),
         dataType: "json",
         success: function(response){
   
           if(response.code == "200"){
   
               $(".dataTables_empty").addClass('hide');
               
   
               $("#lowest-price-order-table tbody tr").removeClass('selected');
             //   <#--  alert(product_name + " " +
             //   String(qty) + " 개가 \n" +
             //   "입하처리 되었습니다.")  -->
             button.disabled = false;
             alert("작업 성공적으로 완료되었습니다.");
               
           } else if(response.code == "500"){
                alert(response.message);
           } else {
               alert("발주 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)");
               button.disabled = false;
           }
   
         }
       });
       
   });
   
   $("#lowest-price-order-table").on("keyup"," input[name^=text]", function(){
     var $tr = $(this).closest('tr');
     var carton_unit = Number($tr.find('td:eq(5) input').val()); // 카톤단위
     var carton_quantity = Number($tr.find('td:eq(6) input').val()); // 카톤수량
     $tr.find('td:eq(7) input').val(carton_unit*carton_quantity);
 
 
     var total_quantity = Number($tr.find('td:eq(7) input').val()); // 총수량
     var supply_price = parseFloat($tr.find('td:eq(8) input').val()); // 공급가
     var shipping_fee = Number($tr.find('td:eq(9) input').val()); // 배송비
     var res2 = (total_quantity*supply_price)+shipping_fee;
     $tr.find('td:eq(10) input').val(total_quantity*supply_price+shipping_fee);    
   }); 
 });
 
 function sendCheckedIds(){
   var arr = [];
   $("input:checked[name=category").each(function(){
       arr.push($(this).val());
   });
 }