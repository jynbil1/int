$(document).ready(function() {

    var arrivaltable = $('#arrival-table').DataTable({
        "order": [
            [0, "desc"]
        ],
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    var arrivaltable2 = $('#arrival-table-2').DataTable({
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    // var arrivaltable3 = $('#arrivalHistoryTable').DataTable({
    //     "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    // });



    //today inlist
    var wh_arrival_seq_column = arrivaltable2.column(0);
    var wh_arrival_barcode_column = arrivaltable2.column(4);
    wh_arrival_seq_column.visible(!wh_arrival_seq_column.visible());
    wh_arrival_barcode_column.visible(!wh_arrival_barcode_column.visible());

    var cnt = 0;


    $('#arrival-table tbody ').on('click', 'tr', function() {
        $(this).toggleClass('selected');
    });

    $('#arrival-table-2 tbody').on('click', 'tr', function() {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            arrivaltable2.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });

    // var button;
    $('#arrival-product').on('click', function(event) {
        event.preventDefault();
        // $(this).prop('disabled', true);
        var button = this;
        button.disabled = true;

        var cnt2 = arrivaltable.rows('.selected').data().length;

        if (arrivaltable.rows('.selected').data().length < 1) {
            alert("선택된 줄이 없습니다. 입하할 줄을 선택해주세요.");
            button.disabled = false;
            return false;
        }

        var list = [];
        for (var i = 0; i < cnt2; i++) {
            // alert( table.rows('.selected').data().length +' row(s) selected' );

            var selectRowData = arrivaltable.rows('.selected').data();

            var md_order_detail_seq = selectRowData[i][9];
            var product_id = selectRowData[i][2];
            var product_name = selectRowData[i][3];
            var available_qty = parseInt(selectRowData[i][7]);
            var StockingArrivalID = "#stocking" + md_order_detail_seq;
            var OperationArrivalID = "#operation" + md_order_detail_seq;
            var expDateID = "#expdate" + md_order_detail_seq;
            var printQtyID = "#print" + md_order_detail_seq;
            var StockingArrivalQty = parseInt($(StockingArrivalID).val());
            var OperationArrivalQty = parseInt($(OperationArrivalID).val());
            var expDateValue = $(expDateID).val();
            var printQty = parseInt($(printQtyID).val());
            console.log(StockingArrivalID)
            console.log(OperationArrivalID)
            console.log(printQtyID)
            console.log(StockingArrivalQty)
            console.log(OperationArrivalQty)
            console.log(printQty)
            if (StockingArrivalQty <= 0 && OperationArrivalQty <= 0) {
                alert(product_name + "의 입하수량을 입력해주세요");
                button.disabled = false;
                return false;
            }

            if (expDateValue == '') {
                alert(product_name + "의 유효기간이 입력되지 않았습니다. \n 유효기간이 없는 상품이면 무시해도 됩니다.");
            }

            var content = {

                wh_arrival_productid: product_id,
                product_name: product_name,
                stocking_arrival_qty: StockingArrivalQty,
                operation_arrival_qty: OperationArrivalQty,
                wh_arrival_expdate: expDateValue,
                md_order_detail_seq: md_order_detail_seq,
                print_label_qty: printQty

            };

            list.push(content);
        }
        console.log(list);
        $(".div-left-title > span").html('입하중...<img alt="loading" style="vertical-align: middle;" src="/assets/img/loading-2.gif">').css("display", "table-cell");
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/arrivalProduct',
            data: JSON.stringify(list),
            dataType: "json",
            success: function(response) {

                var successList = response.firstObject;
                var failList = response.secondObject;
                console.log("successList")
                console.log(successList)
                console.log("failList")
                console.log(failList)

                // var printlist=[];
                // let map = new Map();
                let map = {}
                successList.forEach(function(arrivalproduct, index, array) {
                    // console.log(arrivalproduct, index);

                    $(".dataTables_empty").addClass('hide');
                    var wh_arrival_seq = arrivalproduct.wh_arrival_seq;
                    var product_name = arrivalproduct.product_name;
                    var qty = arrivalproduct.wh_arrival_qty;
                    var printLabelQty = arrivalproduct.print_label_qty;
                    var date;
                    var exp_date = null;

                    if (arrivalproduct.wh_arrival_expdate) {
                        date = new Date(arrivalproduct.wh_arrival_expdate);
                        exp_date = date.getFullYear() + ". " + (date.getMonth() + 1) + ". " + date.getDate();
                    }

                    var barcode = arrivalproduct.wh_arrival_barcode;
                    var excutor = arrivalproduct.create_user;
                    var excution_time = new Date(arrivalproduct.create_time);

                    // console.log(product_name);
                    arrivaltable2.row.add([wh_arrival_seq, product_name, qty, exp_date, barcode, excutor, excution_time]).draw(false);
                    if (wh_arrival_seq_column.visible()) {
                        wh_arrival_seq_column.visible(!wh_arrival_seq_column.visible());
                    }
                    if (wh_arrival_barcode_column.visible()) {
                        wh_arrival_barcode_column.visible(!wh_arrival_barcode_column.visible());
                    }

                    $("#arrival-table tbody tr").removeClass('selected');

                    displayMessage(product_name + " " +
                        String(qty) + " 개가 \n" +
                        "입하처리 되었습니다.");
                    // map.set(wh_arrival_seq, printLabelQty);
                    map[wh_arrival_seq] = printLabelQty;

                });
                console.log(map);

                validateMultipleArrivalLabels(map);

                button.disabled = false;

                failList.forEach(function(arrivalproduct, index, array) {
                    console.log(arrivalproduct, index);
                    var product_name = arrivalproduct.product_name;
                    alert(product_name + "이 실패 하였습니다. ");
                    button.disabled = false;
                });

            }
        });

        // location.reload();
    });
    // button.disabled = false;

    $('#delete-arrival-product').on('click', function(event) {
        event.preventDefault();
        var button = this;
        button.disabled = true;


        if (arrivaltable2.rows('.selected').data().length < 1) {
            alert("선택된 줄이 없습니다. 입고취소할 줄을 선택해주세요.");
            button.disabled = false;
            return false;
        }

        var selectRowData = arrivaltable2.rows('.selected').data();

        var content = {

            wh_arrival_seq: selectRowData[0][0]

        };

        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/deleteArrivalProduct',
            data: JSON.stringify(content),
            dataType: "json",
            success: function(response) {

                if (response.code == "200") {

                    alert("입하삭제 성공");

                    arrivaltable2.rows('.selected').remove().draw();

                    var cntData = arrivaltable2.rows().data().length;

                    if (cntData == 0) {
                        console.log("success");
                        $(".dataTables_empty").addClass('show');
                    } else {
                        $(".dataTables_empty").addClass('hide');
                    }

                    button.disabled = false;

                } else {
                    alert("입하삭제 실패 (세션확인 했음에도 실패하면 관리자에게 문의해주세요)");
                    button.disabled = false;
                }

            }
        });

    });

    $(window).on("load", function() {
        $(".loader-wrapper").fadeOut("slow");
    });

    $(".ship-tracking-btn").on("click", function() {
        orderDetailSeq = $(this).parent().data('id');
        getshipmentTrackingBySeq(orderDetailSeq);

    });

    // Deafult Printer
    setPrinter({ host: ip, port: port });
});

function getshipmentTrackingBySeq(orderSeq) {
    console.log(orderSeq);
    var data = {
        keyType: "orderDetailSeq",
        seq: orderSeq
    };
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/mfc/shipment/getshipmentTrackingBySeq',
        data: JSON.stringify(data),
        dataType: "json",
        success: function(response) {
            console.log(response.code);
            if (response.code == "200") {
                let shipmentStatus = {};
                const jsonObject = response.object;
                console.log(response.object);
                var tr = '';
                $.each(jsonObject, function(i, object) {
                    // var productSkuPerTracking = object.products.length;
                    // console.log(productSkuPerTracking);
                    var carrier_name = object.carrier;
                    var tracking_no = object.tracking_no;

                    // var carrier = object.carrier_site === undefined ? carrier_name : '<a href="' + object.carrier_site + '">' + carrier_name + "</a>"
                    // var tracking = object.tracking_url === undefined ? tracking_no : '<a href="' + object.tracking_url + '">' + tracking_no + "</a>"

                    var trackingUrl = object.urls.tracking === null ? tracking_no : '<a href="' + object.urls.tracking + '">' + tracking_no + "</a>"
                    var officialUrl = object.urls.official === null ? carrier_name : '<a href="' + object.urls.official + '">' + carrier_name + "</a>"

                    shipmentStatus[object.urls.tracking] = [carrier_name, tracking_no];

                    $.each(object.products, function(i, object) {
                        tr += '<tr id="' + object.order_detail_seq + '">' +
                            '<td class="carrier">' + officialUrl + '</td>' +
                            '<td class="tracking-no">' + trackingUrl + '</td>' +
                            '<td class="order-seq">' + object.order_seq + '</td>' +
                            '<td class="order-detail-seq" >' + object.order_detail_seq.substr(6, 4) + "-" + object.product_id + '</td>' +
                            '<td class="product-name"><a href="/md-edit-product?product_id=' + object.product_id + '">' + object.product_name + '</a></td>' +
                            '<td class="status" data-status="' + tracking_no + '">.</td>' +
                            '<td class="status-loc" data-status-loc="' + tracking_no + '">.</td>' +
                            '<td class="status-date" data-status-date="' + tracking_no + '">.</td>' +
                            '</tr>';
                    });
                });
                // already filtered for its duplicatable values
                $.each(shipmentStatus, function(key, value) {
                    getShipmentStatus({ url: key, carrier: value[0], trackingNo: value[1] });
                });

                $("#tracking-modal-content-body").html(tr);
            } else {
                var tr = '<tr><td class="no-product" colspan="8">No records found.</td></tr>';
                $("#tracking-modal-content-body").html(tr);
            }
            openTrackShipmentModal();
        },
        error: function(response) {
            var tr = '<tr><td class="no-product" colspan="8">No records found.</td></tr>';
            $("#tracking-modal-content-body").html(tr);
            openTrackShipmentModal();
        }
    });
}

function getShipmentStatus(shipmentData) {
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/mfc/shipment/getShipmentStatus',
        data: JSON.stringify(shipmentData),
        dataType: "json",
        success: function(response) {
            const object = response.object;
            if (object.status !== '미발송') {
                $('td[data-status="' + shipmentData['trackingNo'] + '"]').text(object.status);
                $('td[data-status-loc="' + shipmentData['trackingNo'] + '"]').text(object.location);
                $('td[data-status-date="' + shipmentData['trackingNo'] + '"]').text(object.datetime);
            } else {
                $('td[data-status="' + shipmentData['trackingNo'] + '"]').text(object.status);
                $('td[data-status-loc="' + shipmentData['trackingNo'] + '"]').text('');
                $('td[data-status-date="' + shipmentData['trackingNo'] + '"]').text('');
            }

        },
        error: function(response) {

        }
    });
}

// function validateMultipleArrivalLabels(arrivalSeqArray) {
//     console.log(arrivalSeqArray);
//     dataSet = { arrivalSeqs: arrivalSeqArray }
function validateMultipleArrivalLabels(map) {
    $(".div-left-title > span").html('인쇄중...<img alt="loading" style="vertical-align: middle;" src="/assets/img/loading-2.gif">').css("display", "table-cell");
    $.ajax({
        url: '/arrival/validateMultipleLabels',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(map),
        // data: map,
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                if ($("#qz-status").text() === "Active") {
                    if ($('#configPrinter').text().indexOf("HOST") != -1) {
                        displayMessage("원격 라벨 인쇄를 시작합니다.");
                        printPDFArrivalZPLLabel(response.message);
                    } else if ($('#configPrinter').text().indexOf("PDF") != -1) {
                        displayMessage("라벨 프린터와 연결되지 않았음으로 물표를 다운로드 합니다.");
                        const linkSource = `data:application/pdf;base64,${response.message}`;
                        const downloadLink = document.createElement("a");
                        const fileName = wh_arrival_seq + ".pdf";
                        downloadLink.href = linkSource;
                        downloadLink.download = fileName;
                        downloadLink.click();
                    } else {
                        displayMessage("라벨 인쇄를 시작합니다.");
                        printPDFArrivalBase64Label(response.message);
                    }
                } else {
                    displayMessage("라벨 프린터와 연결되지 않았음으로 물표를 다운로드 합니다.");
                    console.log(response.message);
                    const linkSource = `data:application/pdf;base64,${response.message}`;
                    const downloadLink = document.createElement("a");
                    const fileName = new Date().format('HH:mm:ss') + ".pdf";
                    // var fileName = wh_arrival_seq + ".pdf";

                    downloadLink.href = linkSource;
                    downloadLink.download = fileName;
                    downloadLink.click();
                    printPDF();
                }
                $(".div-left-title > span").html('').css("display", "none");
            } else {
                alert("An error has occurred.");
            }
        }
    });
}

function closeTrackShipmentModal() {
    $(".tracking-info-modal").css("visibility", "hidden");
    $(".tracking-info-modal").css("opacity", "0");
}

function openTrackShipmentModal() {
    setTimeout(function() {
        mergeTableCells("#tracking-info-table");
        $(".tracking-info-modal").css("visibility", "visible");
        $(".tracking-info-modal").css("opacity", "1");
    }, 500);
}



Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
    var weekKorName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var weekKorShortName = ["일", "월", "화", "수", "목", "금", "토"];
    var weekEngName = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var weekEngShortName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    var d = this;

    return f.replace(/(yyyy|yy|MM|dd|KS|KL|ES|EL|HH|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy":
                return d.getFullYear(); // 년 (4자리)
            case "yy":
                return (d.getFullYear() % 1000).zf(2); // 년 (2자리)
            case "MM":
                return (d.getMonth() + 1).zf(2); // 월 (2자리)
            case "dd":
                return d.getDate().zf(2); // 일 (2자리)
            case "KS":
                return weekKorShortName[d.getDay()]; // 요일 (짧은 한글)
            case "KL":
                return weekKorName[d.getDay()]; // 요일 (긴 한글)
            case "ES":
                return weekEngShortName[d.getDay()]; // 요일 (짧은 영어)
            case "EL":
                return weekEngName[d.getDay()]; // 요일 (긴 영어)
            case "HH":
                return d.getHours().zf(2); // 시간 (24시간 기준, 2자리)
            case "hh":
                return ((h = d.getHours() % 12) ? h : 12).zf(2); // 시간 (12시간 기준, 2자리)
            case "mm":
                return d.getMinutes().zf(2); // 분 (2자리)
            case "ss":
                return d.getSeconds().zf(2); // 초 (2자리)
            case "a/p":
                return d.getHours() < 12 ? "오전" : "오후"; // 오전/오후 구분

            default:
                return $1;
        }
    });
};
String.prototype.string = function(len) {
    var s = '',
        i = 0;
    while (i++ < len) { s += this; }
    return s;
};
String.prototype.zf = function(len) { return "0".string(len - this.length) + this; };
Number.prototype.zf = function(len) { return this.toString().zf(len); };

function mergeTableCells(x) {
    $(x).each(function() {
        $(x).find('td').each(function() {
            var $this = $(this),
                y = $this.index(),
                z = $this.html(),
                rs = 1,
                tc = $($this.parent().prev().children()[y]);

            while (tc.html() === z) {
                rs += 1;
                tc_old = tc;
                tc = $(tc.parent().prev().children()[y]);
            }

            if (rs > 1) {
                $(tc_old).attr('rowspan', rs);
                $this.hide();
            }
        });
    });
}