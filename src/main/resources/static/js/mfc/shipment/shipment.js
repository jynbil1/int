let searchParams = new URLSearchParams(window.location.search);
var defaultCarrier;
let orderSeq;

$(document).ready(function() {
    orderSeq = searchParams.get("order_seq");

    loadProductData(orderSeq);
    getInitialOrderSetting(orderSeq);

    $(".add-shipment-list").click(function() {
        createNewShipment();
        var total = $('ul.shipment-list').length;
        if (total == 15) {
            $(this).hide();
        }
    });

    $(".add-all-to-shipment").click(function() {
        if ($("input.select-product-chk:checked").length > 0) {
            $("#shipmentPopup").addClass("active");
        } else {
            $(".notification-message").addClass("active");
            $(".notification-message.active span").removeClass("success").addClass("error");
            $(".notification-message.active span").html("상품을 먼저 선택해주세요.");
            setTimeout(function() {
                $(".notification-message.active span").removeClass("error");
                $('.notification-message').removeClass('active');
            }, 4000);
        }
    });

    $(".close").click(function(event) {
        event.preventDefault();
        $("#shipmentPopup").removeClass("active");
    });

    $("#selectAll").click(function() {
        $('input:checkbox.select-product-chk').not(this).prop('checked', this.checked);
    });

    $('#confirmTransaction').click(function() {
        var checked = $(this).is(':checked');
        if (checked) {
            if (!confirm("본 발주서의 계산서는 발행 되었습니까?\n(아직 발행 전 이라면, '취소' 를 눌러 주세요.)")) {
                $(this).prop('checked', false);
            } else {
                validateBill(orderSeq, searchParams.get("certIDFirst"), searchParams.get("certIDSecond"));
            }
        }
    });

    $("#submitShipmentOrder").click(function() {
        updateShipment();
        submitShipmentOrder();
    });
});

function loadProductData(orderSeq) {
    $.ajax({
        url: '/mfc/shipment/orderDetailProducts',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({ orderSeq: orderSeq }),
        success: function(response) {
            console.log(response);
            if (response.code == 200) {
                var subtotal = 0,
                    vat_total = 0,
                    shipping_fee_total = 0,
                    grand_total = 0,
                    sum_order_qty = 0,
                    sum_supply_price = 0,
                    sum_vat = 0,
                    sum_shipping_fee_sup = 0,
                    sum_shipping_fee_vat = 0;
                const jsonObject = response.object;
                var tr = '',
                    tfoot = '';
                $.each(jsonObject, function(i, object) {
                    tr += '<tr id="' + object.product_id + '" data-order-detail-seq="' + object.order_detail_seq + '">' +
                        '<td class="select-product"><input type="checkbox" class="select-product-chk"></td>' +
                        '<td class="product-id" data-label="상품 ID" style="display: none;">' + object.product_id + '</td>' +
                        '<td class="product-name" data-label="상품 명">' + object.product_name + '</td>' +
                        '<td class="unit-price" data-label="Unit Price">' + addCommas(object.unit_price) + '</td>' +
                        '<td class="order-qty" data-label="Order QTY">' + addCommas(object.order_qty) + '</td>' +
                        '<td class="order-price" data-label="Price">' + addCommas(object.supply_price) + '</td>' +
                        '<td class="vat" data-label="Vat">' + addCommas(object.vat) + '</td>' +
                        '<td class="shipping-fee" data-label="Shipping Fee">' + addCommas((parseInt(object.shipping_fee_sup + object.shipping_fee_vat))) + '</td>' +
                        '<td class="line-total" data-label="Line Total">' + addCommas(parseInt(object.supply_price + object.vat + object.shipping_fee_sup + object.shipping_fee_vat)) + '</td>' +
                        '<td data-label="Add to Shipment" class="add-to-shipment">' +
                        '<select id="selectShipment" name="select-shipment" class="select-shipment"><option value="0">운송장 선택</option></select></td>' +
                        '<td class="add-to-shipment-btn"><input type="button" class="primary button" value="추가" id="addToShipment"></td>' +
                        '</tr>';

                    sum_order_qty += object.order_qty;
                    sum_supply_price += object.supply_price;
                    sum_vat += object.vat;
                    sum_shipping_fee_sup += object.shipping_fee_sup;
                    sum_shipping_fee_vat += object.shipping_fee_vat;
                });

                shipping_fee_total = (parseInt(sum_shipping_fee_sup + sum_shipping_fee_vat));
                subtotal = (parseInt(sum_supply_price));
                vat_total = sum_vat;
                grand_total = (parseInt(subtotal + vat_total + shipping_fee_total));

                tfoot = '<tfoot><tr><td colspan="12"><div class="tfoot-total"><label>소계:</label><span>' + addCommas(subtotal) +
                    ' 원</span></div></td></tr><tr><td colspan="12"><div class="tfoot-total"><label>부가세:</label><span>' +
                    addCommas(vat_total) + ' 원</span></div></td></tr><tr><td colspan="12"><div class="tfoot-total"><label>배송비:</label><span>' + addCommas(shipping_fee_total) +
                    ' 원</span></div></td></tr><tr><td colspan="12"><div class="tfoot-total"><label>총계:</label><span>' +
                    addCommas(grand_total) + ' 원</span></div></td></tr></tfoot>';

                $('tbody#productList').html(tr);
                $(tfoot).insertAfter('tbody#productList');

                $(".add-to-shipment-btn > .button").click(function() {
                    var shipmentID = $(this).parent().siblings().find(".select-shipment").val();
                    var productID = $(this).parent().parent().attr("id");
                    var productName = $(this).parent().parent().find(".product-name").text();
                    var orderDetailSeq = $(this).parent().parent().data("order-detail-seq");
                    var tr = '<tr id="' + productID + '" class="ui-draggable ui-draggable-handle" data-order-detail-seq="' + orderDetailSeq + '"><td class="product-id" data-label="상품 ID">' + productID + '</td><td class="product-name" data-label="상품 명">' + productName + '</td><td class="delete-product-list"><input type="button" value="X" class="delete-product disabled button"></td></tr>';

                    if (shipmentID == 0) {
                        $(".notification-message").addClass("active");
                        $(".notification-message.active span").removeClass("success").addClass("error");
                        $(".notification-message.active span").html("운송장을 선택해 주세요.");
                        setTimeout(function() {
                            $(".notification-message.active span").removeClass("error");
                            $('.notification-message').removeClass('active');
                        }, 4000);
                    } else {
                        if ($("#shipmentID_" + shipmentID + " table tbody#shipmentList").find("#" + productID).length) {
                            $(".notification-message").addClass("active");
                            $(".notification-message.active span").removeClass("success").addClass("error");
                            $(".notification-message.active span").html("선택하신 상품은 이미 운송장 내 존재합니다.");
                            setTimeout(function() {
                                $(".notification-message.active span").removeClass("error");
                                $('.notification-message').removeClass('active');
                            }, 4000);
                        } else {
                            $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find(".drop-it-here").remove();
                            $("#shipmentID_" + shipmentID + " table tbody#shipmentList").append(tr);
                            $(this).parent().parent().addClass("added").appendTo('tbody#productList');

                            var skuCount = $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find("tr").length;
                            $("#shipmentID_" + shipmentID + " table tbody#shipmentList").parent().prev().find("span.sku").text(skuCount);

                            $(".delete-product").off('click').on('click', function() {
                                var skuCount = Math.ceil($(this).parent().parent().parent().find("tr").length - 1);
                                var productID = $(this).parent().parent().attr("id");
                                $(this).parent().parent().parent().parent().prev().find("span.sku").text(skuCount);

                                if (skuCount == 0) {
                                    $(this).parent().parent().parent().append('<tr class="drop-it-here"> <td colspan="12" class="drop-here">상품을 끌어놔주세요</td></tr>');
                                }

                                $(this).parent().parent().remove();

                                if ($("table tbody#shipmentList").find("#" + productID).length == 0) {
                                    $("tbody#productList").find("#" + productID).removeClass("added");
                                }
                            });
                        }
                    }
                });
                loadShipmentData(orderSeq);
            } else if (response.code == 404) {
                window.location.href = "/mf/shipment/error-page?message=" + response.message;
            } else {
                console.log("An error has occurred.");
            }
        }
    });
}

function validateBill(orderSeq, cert_1, cert_2) {
    data = {
        order_seq: orderSeq,
        certIDFirst: cert_1,
        certIDSecond: cert_2
    }
    $.ajax({
        url: '/mfc/shipment/confirmBillValidation',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $('#confirmTransaction').prop('checked', true);
                $('#confirmTransaction').prop('disabled', true);

                $(".notification-message").addClass("active");
                $(".notification-message.active span").removeClass("error").addClass("success");
                $(".notification-message.active span").html("성공: 계산서 발행 여부를 담당자에게 전달했습니다.");
                setTimeout(function() {
                    $(".notification-message.active span").removeClass("success");
                    $('.notification-message').removeClass('active');
                }, 4000);
            } else if (response.code == '403') {
                $(".notification-message").addClass("active");
                $(".notification-message.active span").removeClass("success").addClass("error");
                $(".notification-message.active span").html("실패: 요청 방식이 잘못되었습니다.");
                setTimeout(function() {
                    $(".notification-message.active span").removeClass("error");
                    $('.notification-message').removeClass('active');
                }, 4000);
            } else {
                $(".notification-message").addClass("active");
                $(".notification-message.active span").removeClass("error").addClass("success");
                $(".notification-message.active span").html("실패: 삭제되었거나, 만료된 발주서인지 확인해주세요.");
                setTimeout(function() {
                    $(".notification-message.active span").removeClass("success");
                    $('.notification-message').removeClass('active');
                }, 4000);
            }

        }
    });
}

function getInitialOrderSetting(orderSeq) {

    $.ajax({
        url: '/mfc/shipment/getInitialOrderSetting',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({ orderSeq: orderSeq }),
        success: function(response) {
            if (response.code == "200") {
                const setting = response.object;
                // setting.is_bill_validated (1 or 0)
                if (setting.is_bill_validated === 1) {
                    $('#confirmTransaction').prop("checked", true)
                    $('#confirmTransaction').prop("disabled", true)
                }
                // every creation of new shipment should have as a default.

                defaultCarrier = setting.def_carrier == null ? "CJ" : setting.def_carrier;
            } else {
                // do nothing.
                console.log(response);
            }
        },
        error: function() {
            console.log("error.")
        }
    });
}

function loadShipmentData(orderSeq) {
    $.ajax({
        url: '/mfc/shipment/getOrderShipments',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({ orderSeq: orderSeq }),
        success: function(response) {
            if (response.code == 200) {
                $('select.select-shipment').empty().prepend("<option value='0'>선택</option>");;
                $("#shipmentListModal").empty();
                if (response.object.length) {
                    $("#submitShipmentOrder").prop("disabled", false);
                    $("#submitShipmentOrder").removeClass("disabled").addClass("primary");

                    const shipment_list = response.object;
                    $("#hiddenOriginalValue").val(JSON.stringify(shipment_list));
                    var orderSeq = searchParams.get("order_seq");
                    var carrier = '';
                    $.each(shipment_list, function(i, object) {
                        carrier = object.carrier == null ? "CJ" : object.carrier;
                        var el = '',
                            tr = '';
                        $.each(object.shipProductsVo, function(i, object) {
                            var product_id = object.product_id;
                            var product_name = object.product_name;
                            var order_detail_seq = object.order_detail_seq;
                            tr += '<tr id="' + product_id + '" class="ui-draggable ui-draggable-handle" data-order-detail-seq="' + order_detail_seq + '"><td class="product-id" data-label="상품 ID">' + product_id + '</td><td class="product-name" data-label="상품 명">' + product_name + '</td><td class="delete-product-list"><input type="button" value="X" class="delete-product disabled button"></td></tr>';

                            $("tbody#productList tr#" + product_id).addClass("added").appendTo('tbody#productList');
                        });

                        var carrierList = ["CJ", "천일", "CU", "대신", "합동", "일양", "건영", "경동",
                            "로젠", "농협", "우체국", "우리", "롯데", "한진", "직접/용달 및 운송장 없음"
                        ];
                        carrierList.sort();

                        el = '<ul class="shipment-list" id="shipmentID_' + object.ship_id + '"> \
                            <li> <ul class="shipment-details">  \
                            <li> \
                                <label for="shipment-no">발송물 번호:</label>  \
                                <input type="text" id="shipment-no" name="shipment-no" placeholder="운송물 번호" value="' + orderSeq + "-" + object.ship_id + '" data-shipment-id="' + object.ship_id + '" disabled> \
                                <input type="button" class="submit secondary button delete-shipment" value="X" data-id="' + object.ship_id + '"> \
                            </li> \
                            <li> \
                            <div class="input-row"> \
                                <div class="col"> \
                                    <div class="input-row two-col"> \
                                        <div class="col"> \
                                            <label for="tracking-no">운송장 번호:</label> \
                                            <input type="text" id="tracking-no" name="tracking-no" placeholder="운송장 번호" value="' + object.tracking_no + '"> \
                                        </div> \
                                        <div class="col"> \
                                            <label for="carrier">배송사</label> \
                                            <select id="carrier" name="carrier" class="carrier"></select> \
                                        </div> \
                                    </div> \
                                </div> \
                            </div> \
                            </li> \
                            <li> \
                            <label>상품 종류: \
                                <span class="sku"></span> 가지 \
                            </label> \
                            <table class="regular-table shipment-list"> \
                                <thead> <tr> <th>상품 ID</th> <th>상품 명</th> </tr></thead> \
                                <tbody id="shipmentList" class="ui-droppable"> ' + tr + '\
                                </tbody> \
                            </table> \
                            </li></ul> \
                            </li></ul>';

                        var total = $('ul.shipment-list').length;

                        if (total == 0) {
                            $(el).insertAfter(".shipment-placeholder");
                            dragAndDropRows();
                            tr = '';
                            $("#shipmentID_" + object.ship_id).find("select[name='carrier']").select2({
                                data: carrierList
                            }).val(carrier).trigger('change');
                        } else {
                            $('ul.shipment-list').each(function(index) {
                                if (index === total - 1) {
                                    $(el).insertAfter(this);
                                    dragAndDropRows();
                                    tr = '';
                                    $("#shipmentID_" + object.ship_id).find("select[name='carrier']").select2({
                                        data: carrierList
                                    }).val(carrier).trigger('change');
                                }
                            });
                        }

                        $('select.select-shipment').each(function() {
                            $(this).append($('<option>', {
                                value: object.ship_id,
                                text: '발송물 번호: ' + object.ship_id
                            }));
                        });

                        $("#shipmentListModal").append('<li><span class="button primary shipment-list-modal" data-shipment-id="' + object.ship_id + '">발송물 번호: ' + object.ship_id + '</span></li>');

                        $(".delete-shipment").off('click').on('click', function() {
                            var ship_id = $(this).data("id");
                            var container = $(this).closest(".shipment-list");
                            if (confirm("해당 운송장을 정말 삭제 하시겠습니까?")) {
                                $(this).attr("disabled", true).removeClass("secondary").addClass("disabled");
                                $.ajax({
                                    url: '/mf/shipment/deleteShipment',
                                    type: 'POST',
                                    data: JSON.stringify({ ship_id: ship_id }),
                                    contentType: "application/json; charset=utf-8",
                                    dataType: "json",
                                    success: function(response) {
                                        if (response.code == '200') {
                                            container.remove();
                                            $(".shipment-placeholder").addClass("active");
                                            $(".shipment-placeholder.active span").addClass("success");
                                            $(".shipment-placeholder.active span").html(response.message);
                                            setTimeout(function() {
                                                $(".shipment-placeholder.active span").removeClass("success");
                                                $('.shipment-placeholder').removeClass('active');
                                            }, 4000);

                                            $(".select-shipment option[value='" + ship_id + "']").remove();
                                            $("ul#shipmentListModal").find("li [data-shipment-id='" + ship_id + "']").parent().remove();

                                            if ($("ul.shipment-list").length <= 0) {
                                                $("#submitShipmentOrder").prop("disabled", true);
                                                $("#submitShipmentOrder").removeClass("primary").addClass("disabled");
                                            }
                                        } else {
                                            console.log("An error has occurred");
                                        }
                                    }
                                });
                            }
                        });

                        $(".delete-product").off('click').on('click', function() {
                            var skuCount = Math.ceil($(this).parent().parent().parent().find("tr").length - 1);
                            var productID = $(this).parent().parent().attr("id");
                            $(this).parent().parent().parent().parent().prev().find("span.sku").text(skuCount);

                            if (skuCount == 0) {
                                $(this).parent().parent().parent().append('<tr class="drop-it-here"> <td colspan="12" class="drop-here">상품을 끌어놔주세요.</td></tr>');
                            }

                            $(this).parent().parent().remove();

                            if ($("table tbody#shipmentList").find("#" + productID).length == 0) {
                                $("tbody#productList").find("#" + productID).removeClass("added");
                            }
                        });

                        $(".shipment-list-modal").click(function() {
                            var shipmentID = $(this).data("shipment-id");
                            var product_list = [];
                            var shipment_list = [];
                            var final_list = [];
                            var tr;

                            if ($("input.select-product-chk:checked").length > 0) {
                                $.each($("input.select-product-chk:checked"), function() {
                                    var product_id = $(this).parent().parent().attr("id");
                                    var product_name = $(this).parent().parent().find(".product-name").text();
                                    var order_detail_seq = $(this).parent().parent().data("order-detail-seq");

                                    var jsonSeqNo = {
                                        'product_id': product_id,
                                        'product_name': product_name,
                                        'order_detail_seq': order_detail_seq
                                    };
                                    product_list.push(jsonSeqNo);
                                    $(this).parent().parent().addClass("added");
                                });

                                $.each($("#shipmentID_" + shipmentID + " table tbody#shipmentList tr").not(".drop-it-here"), function() {
                                    var product_id = $(this).attr("id");
                                    var product_name = $(this).find("td.product-name").text();
                                    var order_detail_seq = $(this).data("order-detail-seq");

                                    var jsonSeqNo = {
                                        'product_id': product_id,
                                        'product_name': product_name,
                                        'order_detail_seq': order_detail_seq
                                    };
                                    shipment_list.push(jsonSeqNo);
                                });

                                var ids = new Set(product_list.map(d => d.product_id));
                                var final_list = [...product_list, ...shipment_list.filter(d => !ids.has(d.product_id))];

                                product_list = [];
                                shipment_list = [];

                                $("#shipmentID_" + shipmentID + " table tbody#shipmentList").html("");

                                $.each(final_list, function(i, object) {
                                    var product_id = object.product_id;
                                    var product_name = object.product_name;
                                    var order_detail_seq = object.order_detail_seq;

                                    tr += '<tr id="' + product_id + '" class="ui-draggable ui-draggable-handle" data-order-detail-seq="' + order_detail_seq + '"><td class="product-id" data-label="상품 ID">' + product_id + '</td><td class="product-name" data-label="상품 명">' + product_name + '</td><td class="delete-product-list"><input type="button" value="X" class="delete-product disabled button"></td></tr>';
                                });

                                $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find(".drop-it-here").remove();
                                $("#shipmentID_" + shipmentID + " table tbody#shipmentList").append(tr);

                                final_list = [];

                                var skuCount = $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find("tr").length;
                                $("#shipmentID_" + shipmentID + " table tbody#shipmentList").parent().prev().find("span.sku").text(skuCount);

                                $(".delete-product").off('click').on('click', function() {
                                    var skuCount = Math.ceil($(this).parent().parent().parent().find("tr").length - 1);
                                    var productID = $(this).parent().parent().attr("id");
                                    $(this).parent().parent().parent().parent().prev().find("span.sku").text(skuCount);

                                    if (skuCount == 0) {
                                        $(this).parent().parent().parent().append('<tr class="drop-it-here"> <td colspan="12" class="drop-here">상품을 끌어놔주세요</td></tr>');
                                    }

                                    $(this).parent().parent().remove();

                                    if ($("table tbody#shipmentList").find("#" + productID).length == 0) {
                                        $("tbody#productList").find("#" + productID).removeClass("added");
                                    }
                                });

                                $("#shipmentPopup").removeClass("active");
                            } else {
                                $(".notification-message").addClass("active");
                                $(".notification-message.active span").removeClass("success").addClass("error");
                                $(".notification-message.active span").html("상품을 먼저 선택해주세요.");
                                setTimeout(function() {
                                    $(".notification-message.active span").removeClass("error");
                                    $('.notification-message').removeClass('active');
                                }, 4000);
                            }
                        });

                        $('input[name="tracking-no"]').on('input', function(e) {
                            $(this).val(function(i, v) {
                                return v.replace('-', '');
                            });
                        });

                        $('select[name="carrier"]').change(function() {
                            if ($(this).val() == '직접/용달 및 운송장 없음') {
                                $(this).parent().parent().find('input[name="tracking-no"]').val("N-A");
                                $(this).parent().parent().find('input[name="tracking-no"]').prop("disabled", true);
                            } else {
                                $(this).parent().parent().find('input[name="tracking-no"]').prop("disabled", false);
                            }
                        });
                    });
                }
            } else {
                console.log("An error has occurred.");
            }
        }
    });
}

function createNewShipment() {
    $(".add-shipment-list").css("display", "none");
    $(".add-shipment-list").next().css("display", "block");

    $.ajax({
        url: '/mfc/shipment/createNewShipment',
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            $("#submitShipmentOrder").prop("disabled", false);
            $("#submitShipmentOrder").removeClass("disabled").addClass("primary");

            var shipmentNo = response.object;
            var orderSeq = searchParams.get("order_seq");
            var carrierList = ["CJ", "천일", "CU", "대신", "합동", "일양", "건영", "경동",
                "로젠", "농협", "우체국", "우리", "롯데", "한진", "직접/용달 및 운송장 없음"
            ];
            carrierList.sort();

            el = '<ul class="shipment-list" id="shipmentID_' + shipmentNo + '"> <li> <ul class="shipment-details"> <li> <label for="shipment-no">운송물 번호: </label> <input type="text" id="shipment-no" name="shipment-no" placeholder="운송물 번호: " value="' + orderSeq + "-" + shipmentNo + '" data-shipment-id="' + shipmentNo + '" disabled><input type="button" class="submit secondary button delete-shipment" value="X" data-id="' + shipmentNo + '"></li><li> <div class="input-row"> <div class="col"> <div class="input-row two-col"> <div class="col"> <label for="tracking-no">운송장 번호:</label> <input type="text" id="tracking-no" name="tracking-no" placeholder="운송장 번호"> </div><div class="col"> <label for="carrier">배송사:</label> <select id="carrier" name="carrier" class="carrier"></select> </div></div></div></div></li><li> <label>상품 리스트: <span class="sku"></span></label> <table class="regular-table shipment-list"> <thead> <tr> <th>상품 ID</th> <th>상품 명</th> </tr></thead> <tbody id="shipmentList" class="ui-droppable"> <tr class="drop-it-here"> <td colspan="12" class="drop-here">여기에 끌어주세요.</td></tr></tbody> </table> </li></ul> </li></ul>';

            var total = $('ul.shipment-list').length;

            if (total == 0) {
                $(el).insertAfter(".shipment-placeholder");
                dragAndDropRows();
                $("#shipmentID_" + shipmentNo).find("select[name='carrier']").select2({
                    data: carrierList
                }).val(defaultCarrier).trigger('change');
            } else {
                $('ul.shipment-list').each(function(index) {
                    if (index === total - 1) {
                        $(el).insertAfter(this);
                        dragAndDropRows();
                        $("#shipmentID_" + shipmentNo).find("select[name='carrier']").select2({
                            data: carrierList
                        }).val(defaultCarrier).trigger('change');
                    }
                });
            }

            $('select.select-shipment').each(function() {
                $(this).append($('<option>', {
                    value: shipmentNo,
                    text: '운송물 번호' + shipmentNo
                }));
            });

            $("#shipmentListModal").append('<li><span class="button primary shipment-list-modal" data-shipment-id="' + shipmentNo + '">배송물 번호: ' + shipmentNo + '</span></li>');

            $(".delete-shipment").off('click').on('click', function() {
                var ship_id = $(this).data("id");
                var container = $(this).closest(".shipment-list");
                if (confirm("해당 운송장을 정말 삭제 하시겠습니까?")) {
                    $.ajax({
                        url: '/mf/shipment/deleteShipment',
                        type: 'POST',
                        data: JSON.stringify({ ship_id: ship_id }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function(response) {
                            if (response.code == '200') {
                                container.remove();
                                $(".shipment-placeholder").addClass("active");
                                $(".shipment-placeholder.active span").addClass("success");
                                $(".shipment-placeholder.active span").html(response.message);
                                setTimeout(function() {
                                    $(".shipment-placeholder.active span").removeClass("success");
                                    $('.shipment-placeholder').removeClass('active');
                                }, 4000);

                                $(".select-shipment option[value='" + ship_id + "']").remove();
                                $("ul#shipmentListModal").find("li [data-shipment-id='" + ship_id + "']").parent().remove();

                                if ($("ul.shipment-list").length <= 0) {
                                    $("#submitShipmentOrder").prop("disabled", true);
                                    $("#submitShipmentOrder").removeClass("primary").addClass("disabled");
                                }
                            } else {
                                console.log("An error has occurred");
                            }
                        }
                    });
                }
            });

            $(".shipment-list-modal").click(function() {
                var shipmentID = $(this).data("shipment-id");
                var product_list = [];
                var shipment_list = [];
                var final_list = [];
                var tr;

                if ($("input.select-product-chk:checked").length > 0) {
                    $.each($("input.select-product-chk:checked"), function() {
                        var product_id = $(this).parent().parent().attr("id");
                        var product_name = $(this).parent().parent().find(".product-name").text();
                        var order_detail_seq = $(this).parent().parent().data("order-detail-seq");

                        var jsonSeqNo = {
                            'product_id': product_id,
                            'product_name': product_name,
                            'order_detail_seq': order_detail_seq
                        };

                        product_list.push(jsonSeqNo);
                        $(this).parent().parent().addClass("added");
                    });

                    $.each($("#shipmentID_" + shipmentID + " table tbody#shipmentList tr").not(".drop-it-here"), function() {
                        var product_id = $(this).attr("id");
                        var product_name = $(this).find("td.product-name").text();
                        var order_detail_seq = $(this).data("order-detail-seq");

                        var jsonSeqNo = {
                            'product_id': product_id,
                            'product_name': product_name,
                            'order_detail_seq': order_detail_seq
                        };
                        shipment_list.push(jsonSeqNo);
                    });

                    var ids = new Set(product_list.map(d => d.product_id));
                    var final_list = [...product_list, ...shipment_list.filter(d => !ids.has(d.product_id))];

                    product_list = [];
                    shipment_list = [];

                    $("#shipmentID_" + shipmentID + " table tbody#shipmentList").html("");

                    $.each(final_list, function(i, object) {
                        var product_id = object.product_id;
                        var product_name = object.product_name;
                        var order_detail_seq = object.order_detail_seq;

                        tr += '<tr id="' + product_id + '" class="ui-draggable ui-draggable-handle" data-order-detail-seq="' + order_detail_seq + '"><td class="product-id" data-label="상품 ID">' + product_id + '</td><td class="product-name" data-label="상품 명">' + product_name + '</td><td class="delete-product-list"><input type="button" value="X" class="delete-product disabled button"></td></tr>';
                    });

                    $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find(".drop-it-here").remove();
                    $("#shipmentID_" + shipmentID + " table tbody#shipmentList").append(tr);

                    final_list = [];

                    var skuCount = $("#shipmentID_" + shipmentID + " table tbody#shipmentList").find("tr").length;
                    $("#shipmentID_" + shipmentID + " table tbody#shipmentList").parent().prev().find("span.sku").text(skuCount);

                    $(".delete-product").off('click').on('click', function() {
                        var skuCount = Math.ceil($(this).parent().parent().parent().find("tr").length - 1);
                        var productID = $(this).parent().parent().attr("id");
                        $(this).parent().parent().parent().parent().prev().find("span.sku").text(skuCount);

                        if (skuCount == 0) {
                            $(this).parent().parent().parent().append('<tr class="drop-it-here"> <td colspan="12" class="drop-here">여기에 끌어주세요</td></tr>');
                        }

                        $(this).parent().parent().remove();

                        if ($("table tbody#shipmentList").find("#" + productID).length == 0) {
                            $("tbody#productList").find("#" + productID).removeClass("added");
                        }
                    });

                    $("#shipmentPopup").removeClass("active");
                } else {
                    $(".notification-message").addClass("active");
                    $(".notification-message.active span").removeClass("success").addClass("error");
                    $(".notification-message.active span").html("상품을 먼저 선택해주세요.");
                    setTimeout(function() {
                        $(".notification-message.active span").removeClass("error");
                        $('.notification-message').removeClass('active');
                    }, 4000);
                }
            });

            $('input[name="tracking-no"]').on('input', function(e) {
                $(this).val(function(i, v) {
                    return v.replace('-', '');
                });
            });

            $('select[name="carrier"]').change(function() {
                if ($(this).val() == '직접/용달 및 운송장 없음') {
                    $(this).parent().parent().find('input[name="tracking-no"]').val("N-A");
                    $(this).parent().parent().find('input[name="tracking-no"]').prop("disabled", true);
                } else {
                    $(this).parent().parent().find('input[name="tracking-no"]').prop("disabled", false);
                }
            });

            $(".add-shipment-list").css("display", "block");
            $(".add-shipment-list").next().css("display", "none");
        }
    });
}


function dragAndDropRows() {
    $("#productList tr").draggable({
        helper: "clone",
        revert: 'invalid',
        opacity: "0.5"
    });

    $("ul.shipment-list").droppable({
        accept: $("#productList tr"),
        hoverClass: "dropHover",
        drop: function(ev, ui) {
            var me = ui.draggable.clone();

            if ($(this).find("tbody").find("#" + $(me).attr("id")).length) {
                $(".notification-message").addClass("active");
                $(".notification-message.active span").removeClass("success").addClass("error");
                $(".notification-message.active span").html("해당 상품은 운송장 내 이미 담겨 있습니다.");
                setTimeout(function() {
                    $(".notification-message.active span").removeClass("error");
                    $('.notification-message').removeClass('active');
                }, 4000);
            } else {
                $(this).find("tbody").find(".drop-it-here").remove();
                $(ui.draggable).addClass("added").appendTo('tbody#productList');
                me.removeClass("added");
                me.children("td.select-product").remove();
                me.children("td.order-qty").remove();
                me.children("td.order-price").remove();
                me.children("td.vat").remove();
                me.children("td.shipping-fee").remove();
                me.children("td.line-total").remove();
                me.children("td.unit-price").remove();
                me.children("td.add-to-shipment").remove();
                me.children("td.add-to-shipment-btn").remove();
                me.append('<td class="delete-product-list"><input type="button" value="x" class="delete-product disabled button"></td>');
                me.appendTo($(this).find("tbody"));

                var skuCount = $(this).find("tbody").find("tr").length;
                $(this).find("tbody").parent().prev().find("span.sku").text(skuCount);

                $(".delete-product").off('click').on('click', function() {
                    var skuCount = Math.ceil($(this).parent().parent().parent().find("tr").length - 1);
                    var productID = $(this).parent().parent().attr("id");
                    $(this).parent().parent().parent().parent().prev().find("span.sku").text(skuCount);

                    if (skuCount == 0) {
                        $(this).parent().parent().parent().append('<tr class="drop-it-here"> <td colspan="12" class="drop-here">상품을 끌어놔주세요</td></tr>');
                    }

                    $(this).parent().parent().remove();

                    if ($("table tbody#shipmentList").find("#" + productID).length == 0) {
                        $("tbody#productList").find("#" + productID).removeClass("added");
                    }
                });
            }
        }
    });
}

function updateShipment() {
    let shipments = [];
    let shipmentNo;
    let carrier;
    let trackingNo;

    let table_list = $("ul.shipment-list ul.shipment-details .regular-table.shipment-list").length;
    if (table_list > 0) {
        $.each($("ul.shipment-list"), function() {
            shipmentNo = $(this).find("#shipment-no").data("shipment-id");
            carrier = $(this).find("#carrier").val();
            trackingNo = $(this).find("#tracking-no").val().trim();
            if (trackingNo == "" || trackingNo == null) {
                $(this).find("label[for='tracking-no']").text("운송장 번호는 필수 입니다.")
                $(this).find("label[for='tracking-no']").css({ "color": "red", "font-weight": "600" });
                $(this).find("input[name='tracking-no']").css({ "border": "#FF3B2C 2px solid" });
            } else {
                $(this).find("label[for='tracking-no']").text("운송장 번호: ")
                $(this).find("label[for='tracking-no']").css({ "color": "#3F434A", "font-weight": "normal" });
                $(this).find("input[name='tracking-no']").css({ "border": "#E0E0E0 2px solid" });
                shipments.push({ ship_id: shipmentNo, carrier: carrier, tracking_no: trackingNo });
            }
        });
    }

    if (shipments.length > 0) {
        $.ajax({
            url: '/mfc/shipment/updateShipmentDetails',
            type: 'POST',
            data: JSON.stringify(shipments),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(response) {
                if (response.code == '200') {
                    console.log(response.message);
                } else {
                    console.log("An error has occurred");
                }
            }
        });
    } else {
        $(".shipment-placeholder").addClass("active");
        $(".shipment-placeholder.active span").addClass("warning");
        $(".shipment-placeholder.active span").html("No shipment to be submitted, please review.");
        setTimeout(function() {
            $(".shipment-placeholder.active span").removeClass("warning");
            $('.shipment-placeholder').removeClass('active');
        }, 4000);
    }
}

function submitShipmentOrder() {
    var list = { "orderSeq": [], "new": [] };
    var table_list = $("ul.shipment-list ul.shipment-details .regular-table.shipment-list").length;
    // var hdnVal = $("#hiddenOriginalValue").val().trim();
    var empty_product = $("ul.shipment-list ul.shipment-details .regular-table.shipment-list").find("tbody#shipmentList tr.drop-it-here");
    var tracking_input = $("input[name='tracking-no']").filter(function() {
        return $.trim($(this).val()).length == 0
    }).length == 0;
    list.orderSeq.push({ orderSeq: orderSeq });
    // if (hdnVal != "" && hdnVal != null) {
    //     const arr = JSON.parse($("#hiddenOriginalValue").val());
    //     $.each(arr, function(i, object) {
    //         var ship_id = object.ship_id;
    //         var tracking_no = object.ship_id;
    //         var carrier = object.carrier;

    //         $.each(object.shipProductsVo, function(i, object) {
    //             var product_id = object.product_id;
    //             var order_detail_seq = object.order_detail_seq;

    //             var jsonProduct = {
    //                 'ship_id': parseInt(ship_id),
    //                 'carrier': carrier.toString(),
    //                 'product_id': product_id.toString(),
    //                 'tracking_no': tracking_no.toString(),
    //                 'order_detail_seq': order_detail_seq.toString()
    //             };
    //             list.original.push(jsonProduct);
    //         });
    //     });
    // }

    if (table_list > 0) {
        $.each($("ul.shipment-list ul.shipment-details"), function() {
            var $product_list = $(this).find(".regular-table.shipment-list");
            $.each($product_list, function() {
                var products = $(this).find("tbody#shipmentList tr").not(".drop-it-here");
                if ($(this).find("tbody#shipmentList tr").not(".drop-it-here").length) {
                    $.each(products, function() {
                        var ship_id = $(this).parent().parent().parent().parent().find("input[name='shipment-no'").data("shipment-id");
                        var carrier = $(this).parent().parent().parent().parent().find("#carrier").val();
                        var product_id = Number($(this).find("td.product-id").text());
                        var tracking_no = $(this).parent().parent().parent().parent().find("input[name='tracking-no'").val();
                        var order_detail_seq = $(this).data("order-detail-seq");

                        if (tracking_no != "" && tracking_no != null) {
                            var jsonProduct = {
                                'ship_id': parseInt(ship_id),
                                'carrier': carrier.toString(),
                                'product_id': product_id.toString(),
                                'tracking_no': tracking_no.toString(),
                                'order_detail_seq': order_detail_seq.toString()
                            };
                            list.new.push(jsonProduct);
                        }
                    });
                } else {
                    $(".shipment-placeholder").addClass("active");
                    $(".shipment-placeholder.active span").addClass("warning");
                    $(".shipment-placeholder.active span").html("No product is added, please review.");
                    setTimeout(function() {
                        $(".shipment-placeholder.active span").removeClass("warning");
                        $('.shipment-placeholder').removeClass('active');
                    }, 4000);
                }
            });
        });

        if (tracking_input) {
            if (empty_product.length <= 0) {
                $("#submitShipmentOrder").css("display", "none");
                $("#submitShipmentOrder").next("img").css("display", "block");
                $.ajax({
                    url: '/mfc/shipment/recordOrderShipmentLabel',
                    type: 'POST',
                    data: JSON.stringify(list),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function(response) {
                        if (response.code == '200') {
                            $(".shipment-placeholder").addClass("active");
                            $(".shipment-placeholder.active span").addClass("success");
                            $(".shipment-placeholder.active span").html(response.message);
                            setTimeout(function() {
                                $(".shipment-placeholder.active span").removeClass("success");
                                $('.shipment-placeholder').removeClass('active');
                            }, 4000);
                            var orderSeq = searchParams.get("order_seq");
                            $(".right-table > ul.shipment-list").remove();
                            loadShipmentData(orderSeq);
                            window.scrollTo({ top: 0, behavior: "smooth" });
                            $("#submitShipmentOrder").css("display", "block");
                            $("#submitShipmentOrder").next("img").css("display", "none");
                        } else {
                            console.log("An error has occurred");
                            $("#submitShipmentOrder").css("display", "block");
                            $("#submitShipmentOrder").next("img").css("display", "none");
                        }
                    }
                });
            }
            $(this).parent().parent().parent().parent().find("label[for='tracking-no']").text("운송장 번호: ");
            $(this).parent().parent().parent().parent().find("label[for='tracking-no']").css({ "color": "#3F434A", "font-weight": "normal" });
            $(this).parent().parent().parent().parent().find("input[name='tracking-no']").css({ "border": "#E0E0E0 2px solid" });
        } else {
            $(this).parent().parent().parent().parent().find("label[for='tracking-no']").text("운송장 번호는 필수입니다.")
            $(this).parent().parent().parent().parent().find("label[for='tracking-no']").css({ "color": "#FF3B2C", "font-weight": "600" });
            $(this).parent().parent().parent().parent().find("input[name='tracking-no']").css({ "border": "#FF3B2C 2px solid" });
        }
    } else {
        $(".shipment-placeholder").addClass("active");
        $(".shipment-placeholder.active span").addClass("warning");
        $(".shipment-placeholder.active span").html("No shipment to be submitted, please review.");
        setTimeout(function() {
            $(".shipment-placeholder.active span").removeClass("warning");
            $('.shipment-placeholder').removeClass('active');
        }, 4000);
    }
}

function addCommas(input) {
    var parts = input.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}