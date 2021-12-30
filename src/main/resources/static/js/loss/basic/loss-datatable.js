$(document).ready(function() {

    var lossTable = $('#loss-table').DataTable({
        "order": [
            [1, "asc"]
        ],
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    $('#loss-table tbody').on('click', 'tr', function() {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            lossTable
                .$('tr.selected')
                .removeClass('selected');
            $(this).addClass('selected');
        }
    });

    // Product Loss Function
    $('#loss-product').on('click', function(event) {
        event.preventDefault();
        var button = this;
        button.disabled = true;


        // alert(
        //     table.rows('.selected').data().length + ' row(s) selected'
        // );
        var selectedRows = lossTable.rows('.selected').data().length
        if (selectedRows == 0) {
            alert("선택된 데이터가 없습니다.")
        }

        var selectRowData = lossTable
            .rows('.selected')
            .data();

        // var index = selectRowData[0][0];
        console.log(selectRowData)

        var product_id = selectRowData[0][0]

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
            wh_loss_barcode: selectRowData[0][3],
            wh_loss_qty: inputLossQtyValue,

            wh_loss_occured_sector: inputLossSectorValue,
            wh_loss_reason: inputLossReasonValue,
            wh_loss_remark: inputLossRemarkValue,

            wh_loss_exp: inputLossExpValue,
            wh_in_location: inputInLocationValue,

            wh_loss_product_name: selectRowData[0][1],
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
    });

    // Loss Product History Page  
    var recoverLossTable = $('#recover-loss-table').DataTable({
        "order": [
            [8, "desc"]
        ]
    });

    // $('.selected-btn').on('click', function(event){
    //     alert("start");
    //     console.log("loss start");
    //     $('.item-rows').removeClass('selected');
    //     $(event.target).closest(".item-rows").addClass("selected");
    //   });
    $('#recover-loss-table tbody').on('click', 'tr', function() {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            recoverLossTable
                .$('tr.selected')
                .removeClass('selected');
            $(this).addClass('selected');
        }
    });

    $('#recover-loss-product').on('click', function(event) {
        event.preventDefault();
        var button = this;
        button.disabled = true;


        console.log("loss start");
        var selectRowData = recoverLossTable
            .rows('.selected')
            .data();

        var loss_content = {
            wh_loss_seq: selectRowData[0][0],
            wh_loss_productid: selectRowData[0][1],
            wh_loss_qty: selectRowData[0][3]
        };

        console.log(loss_content);
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/cancelLossProductRecord',
            data: JSON.stringify(loss_content),
            dataType: "json",
            success: function(response) {
                console.log(response);
                if (response.code == '200') {
                    recoverLossTable.rows('.selected').remove().draw();
                    var cntData = recoverLossTable.rows().data().length;
                    if (cntData == 0) {
                        console.log("success");
                        $(".data-tables-empty").addClass('show');
                    } else {
                        $(".data-tables-empty").addClass('hide');
                    }
                    alert("삭제 성공");
                    button.disabled = false;
                } else if (response.code == '403') {
                    // If login Session is Expired,
                    window.location.href = "/lossProductHistory";

                } else {
                    alert("데이터 입력 오류");
                    button.disabled = false;
                }

            }
        });
    });

    $(window).on("load", function() {
        $(".loader-wrapper").fadeOut("slow");
    });

});