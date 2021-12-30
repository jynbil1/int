var redirect_;
var summary_details;
var isValidUser;

$(document).ready(function () {

    $(".submit-btn").on( "click", function() {
        var button = this;
        button.disabled = true;
        var list=[];
        $("#unshippableOrderList>tr").each(function () {
            var self = $(this);
            var product_id = self.find("[name='product-id']").val();
            var unshippable_qty =  parseInt(self.find("[name='unshippable-qty']").val());
            var occurred_level = self.find("[name='occurred-level']").val();
            var order_no = self.find("[name='order-no']").val();
            var order_qty =  parseInt(self.find("[name='order-qty']").val());
            
            if (unshippable_qty < 0) {
                alert("이동수량을 양수로 입력해주세요");
                $(this).removeClass("row-green");
                $(this).removeClass("row-white");
                $(this).addClass("row-red");
                button.disabled = false;
                return false;
            } else if (unshippable_qty > order_qty) {
                alert(product_name + "의 미출고 수량이 주문 수량보다 많습니다.");
                $(this).removeClass("row-green");
                $(this).removeClass("row-white");
                $(this).addClass("row-red");
                button.disabled = false;
                return false;
            }

            var formData = {
                md_product_id: product_id
                , need_qty: unshippable_qty
                , occurred_level : occurred_level
                , order_no : order_no
                , order_qty : order_qty
            }

            if(unshippable_qty != 0){
                list.push(formData)
                console.log(list);
                $(this).removeClass("row-white");
                $(this).addClass("row-green");
            }
            
        });

        console.log(list)
        $.ajax({
            url: '/operation/insertUnshippable',
            type:'POST',
            data: JSON.stringify(list),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                if (response.code == '200') {
                    alert("미출고처리완료");
                    button.disabled = false;
                    window.location.href="/operation/order-unshippable-history";
                } else {
                    $("#unshippableOrderList>tr").each(function () {
                        $(this).removeClass("row-green");
                        $(this).removeClass("row-white");
                        $(this).addClass("row-red");
                    });
                    
                    if (response.code == '401'){
                        alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                    }else{
                        alert("서버오류 관리자에게 문의해주세요")
                        alert(response.message)
                    }
                }
            }
        });// end getOneTransaction

    }); //end edit btn'
    
});
