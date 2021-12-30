$(document).ready(function () {

   
    $(".insert-btn").on( "click", function() {
        var button = this;
        button.disabled = true;
        
        var product_id = $(this).attr("data-id");
        var product_name = $(this).parent().parent().find("[name='product-name']").val();
        var barcode = $(this).parent().parent().find("[name='barcode']").val();
        //console.log(md_order_id);
       

        var inputLossQtyID = "#input-loss-qty" + product_id;
        var inputLossExp = "#input-loss-exp" + product_id;
        var inputInLocationID = "#loss-in-location" + product_id;
        var inputLossSectorID = "#loss-sector" + product_id;
        var inputLossReasonID = "#loss-reason" + product_id;
        var inputRemarkID = "#input-remark" + product_id;

        // id -> value
        var inputLossQtyValue = $(inputLossQtyID).val();
        var inputLossExpValue = $(inputLossExp).val();
        var inputInLocationValue = $(inputInLocationID).val();

        var inputLossSectorValue = $(inputLossSectorID).val();
        var inputLossReasonValue = $(inputLossReasonID).val();
        var inputLossRemarkValue = $(inputRemarkID).val();
        console.log(inputInLocationValue)
        var loss_content = {
            wh_loss_productid: product_id,
            wh_loss_barcode: barcode,
            wh_loss_qty: inputLossQtyValue,

            wh_loss_occured_sector: inputLossSectorValue,
            wh_loss_reason: inputLossReasonValue,
            wh_loss_remark: inputLossRemarkValue,

            wh_loss_exp: inputLossExpValue,
            wh_in_location: inputInLocationValue,

            wh_loss_product_name: product_name,
        };

        console.log(loss_content);
        if (!inputLossQtyValue) {
            alert("손실 수량을 입력해 주세요.")
        } else if (inputLossSectorValue == "선택") {
            alert("구역 값을 입력해 주세요.")
        } else if (inputLossReasonValue == "선택") {
            alert("원인 값을 입력해 주세요.")
        } else if (inputLossSectorValue == "보관존" && (!inputInLocationValue)) {
            alert("보관존 구역에서 보관존 위치가 명시되지 않았습니다.")
        } else if (inputLossReasonValue == "부패" && (!inputLossExpValue)) {
            alert("부패 상품인데 명시된 유통기한이 없습니다.")
        } else {

            if (!inputInLocationValue) {
                alert("보관존 위치가 입력되지 않았습니다.\n보관존 발생이 아니면 무시해주세요.")
            }

            if (!inputLossExpValue) {
                alert("손실상품의 유통기한 값이 없습니다.\n유통기한이 없는 상품의 경우 무시해주세요.")
            }

            $.ajax({
                type: 'POST',
                contentType: "application/json",
                url: '/recordLossProduct',
                data: JSON.stringify(loss_content),
                dataType: "json",
                success: function(response) {
                    console.log(response);
                    if (response.code == '200') {
                        $(inputLossQtyID).val('');
                        $(inputLossExp).val('');
                        $(inputInLocationID).val('');

                        $(inputLossSectorID).val('선택');
                        $(inputLossReasonID).val('선택');
                        $(inputRemarkID).val('');

                        alert(response.object.wh_loss_product_name + "\n" +
                            String(response.object.wh_loss_qty) + " 개가 " +
                            response.object.wh_loss_occured_sector + " - " +
                            response.object.wh_loss_reason +
                            "\n (으)로 손실처리 되었습니다.");
                        button.disabled = false;
                    } else {
                        alert("데이터 입력 오류");
                        button.disabled = false;
                    }
                    // if(response.code == '200'){   $(".status44").html("<font color=green
                    // size=0.07px><br>사용 가능한 메일입니다.</font>");   testmail = 1; }else{
                    // $(".status44").html("<font color=red size=0.07px><br>중북된 메일입니다.</font>");
                    // testmail = 0; }

                }
            });
        }
        button.disabled = false;
    }); //end edit btn'

    $(".input-loss-qty").on("keyup", function () {
        var $tr = $(this).closest('tr');
        var val = $(this).val();

        var lossReasonOptions = [
            { value: "발견", text: "발견" },
            { value: "오배송", text: "오배송" },
            { value: "고객환불", text: "고객환불" }
        ];

        var originalLossReasonOptions = [
            { value: "선택", text: "선택" },
            { value: "유통기한 만료", text: "유통기한 만료" },
            { value: "부패", text: "부패" },
            { value: "파손", text: "파손" },
            { value: "DHL파손", text: "DHL파손" },
            { value: "분실", text: "분실" },
            { value: "상품불량", text: "상품불량" },
            { value: "미출고", text: "미출고" },
            { value: "발견", text: "발견" },
            { value: "사은품", text: "사은품" },
            { value: "상품이동", text: "상품이동" },
            { value: "오배송", text: "오배송" },
            { value: "재배송", text: "재배송" },
            { value: "고객환불", text: "고객환불" },
            { value: "내부구매", text: "내부구매" },
            { value: "기타", text: "기타(비고)" }
        ];

        if (val.match(/^-\d+$/) != null && val.match(/^-\d+$/).length > 0) {
            $tr.find('select[name="lossReason"').empty();
            $.each(lossReasonOptions, function (i, t) {
                $tr.find('select[name="lossReason"').append('<option value="' + t.value + '">' + t.text + '</option>');
            });
        } else {
            $tr.find('select[name="lossReason"').empty();
            $.each(originalLossReasonOptions, function (i, t) {
                $tr.find('select[name="lossReason"').append('<option value="' + t.value + '">' + t.text + '</option>');
            });
        }
    });


}); 