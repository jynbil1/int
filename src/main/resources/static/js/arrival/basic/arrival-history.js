$(document).ready(function() {

    $(".delete-btn").on("click", function() {
        var wh_arrival_seq = $(this).attr("data-id");
        var deleteRow = $(this).parent().parent();
        //console.log(md_order_id);

        var formData = {
            wh_arrival_seq: wh_arrival_seq
        }

        console.log(formData);
        $.ajax({
            url: '/deleteArrivalProduct',
            type: 'POST',
            data: JSON.stringify(formData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success: function(response) {
                if (response.code == '200') {
                    $(deleteRow).addClass('hide');
                    alert("삭제성공");
                } else if (response.code == '401') {
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                } else {
                    alert("서버오류 관리자에게 문의해주세요")
                }
            }
        }); // end getOneTransaction

    }); //end edit btn'

    $(".validate-btn").on("click", function() {
        var wh_arrival_seq = $(this).attr("data-id");

        $.ajax({
            url: '/arrival/validateLabel?arrivalSeq=' + wh_arrival_seq,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
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
                        const linkSource = `data:application/pdf;base64,${response.message}`;
                        const downloadLink = document.createElement("a");
                        const fileName = wh_arrival_seq + ".pdf";
                        downloadLink.href = linkSource;
                        downloadLink.download = fileName;
                        downloadLink.click();
                    }
                } else {
                    alert("An error has occurred.");
                }
            }
        });

    });

    //set disable succeeding dates
    var date = new Date();
    var dateToday = [date.getFullYear(), ('0' + (date.getMonth() + 1)).slice(-2), ('0' + date.getDate()).slice(-2)].join('-');

    $("#dateFrom").attr("max", dateToday);
    $("#dateTo").attr("max", dateToday);

    //display date range modal
    $("#dateRange").on("click", function() {
        $("#selectRange").css("visibility", "visible");
        $("#selectRange").css("opacity", "1");
    });

    //disable date range if today date is selected
    $("#dateToday").change(function() {
        if ($(this).is(':checked')) {
            $("#dateFrom").attr("disabled", "true");
            $("#dateTo").attr("disabled", "true");
            $("#dateFrom").css("border", "#E0E0E0 1px solid");
            $("#dateTo").css("border", "#E0E0E0 1px solid");
            $('#dateFrom').val(new Date().toDateInputValue());
            $('#dateTo').val(new Date().toDateInputValue());
        } else {
            $("#dateFrom").removeAttr("disabled");
            $("#dateTo").removeAttr("disabled");
            $("#dateFrom").css("border", "#E0E0E0 1px solid");
            $("#dateTo").css("border", "#E0E0E0 1px solid");
            $('#dateFrom').val("");
            $('#dateTo').val("");
        }
    });

    //set min date 
    $("#dateFrom").change(function() {
        $("#dateTo").val("");
        $("#dateTo").attr("min", $(this).val());
        if ($(this).val() == "") {
            $(this).css("border", "2px solid red");
        } else {
            $(this).css("border", "#E0E0E0 1px solid");
        }
    });

    $("#dateTo").change(function() {
        if ($(this).val() == "") {
            $(this).css("border", "2px solid red");
        } else {
            $(this).css("border", "#E0E0E0 1px solid");
        }
    });

    $("#exportExcel").on("click", function() {
        if ($('#dateToday').is(":checked")) {
            var date = new Date();
            var dateToday = [
                date.getFullYear(),
                ('0' + (date.getMonth() + 1)).slice(-2),
                ('0' + date.getDate()).slice(-2)
            ].join('-');
            downloadArrivedProductListExcelByDate(dateToday, dateToday);
        } else {
            var dateFrom = $("#dateFrom").val();
            var dateTo = $("#dateTo").val();

            if (dateFrom == "") {
                $("#dateFrom").css("border", "2px solid red");
            } else if (dateTo == "") {
                $("#dateTo").css("border", "2px solid red");
            } else {
                var startDate = $("#dateFrom").val();
                var endDate = $("#dateTo").val();
                $("#dateTo").css("border", "#E0E0E0 1px solid");
                $("#dateFrom").css("border", "#E0E0E0 1px solid");
                $("#dateTo").css("border", "#E0E0E0 1px solid");

                downloadArrivedProductListExcelByDate(startDate, endDate);
            }
        }
    });

    // get correct date
    Date.prototype.toDateInputValue = (function() {
        var local = new Date(this);
        local.setMinutes(this.getMinutes() - this.getTimezoneOffset());
        return local.toJSON().slice(0, 10);
    });

    // Deafult Printer
    setPrinter({ host: ip, port: port });
});

function closeDateRange() {
    $("#selectRange").css("visibility", "hidden");
    $("#selectRange").css("opacity", "0");
}

function downloadArrivedProductListExcelByDate(arrived_start_date, arrived_end_date) {
    $.ajax({
        url: '/downloadArrivedProductListExcelByDate?arrivedStartDate=' + arrived_start_date + '&arrivedEndDate=' + arrived_end_date,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: true,
        success: function(response) {
            if (response.code == '200') {
                const linkSource = `data:application/xlsx;base64,${response.message}`;
                const downloadLink = document.createElement("a");
                const fileName = arrived_start_date + ' 00:00:00_' + arrived_end_date + ' 23:59:00_arrived_products' + ".xlsx";
                downloadLink.href = linkSource;
                downloadLink.download = fileName;
                downloadLink.click();
                closeDateRange();
            } else if (response.code == '500') {
                console.log("123");
                $(".notification").fadeIn(500, function() {
                    $(this).fadeOut(4000);
                });
            } else {
                closeDateRange();
                console.log("An error has occurred.");
            }
        },
        error: function() {
            console.log("error");
        }
    });
}