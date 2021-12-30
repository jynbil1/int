//global variables
var item_range, current_page, count, totalPage, sort, order;

$(document).ready(function () {
    item_range = $("#item_range").val();
    current_page = $("#listPage").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    getValidatedListCount();

    $('#item_range').change(function () {
        keyword = $(".search > input").val();
        current_page = 1;
        $("#listPage").val(current_page);
        item_range = $(this).val();
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage);
    });
});

function createPagination(current, total) {
    var delta = 1,
        left = parseInt(current) - delta,
        right = parseInt(current) + delta + 1,
        range = [],
        rangeWithDots = [],
        l;

    for (let i = 1; i <= parseInt(total); i++) {
        if (i == 1 || i == parseInt(total) || i >= left && i < right) {
            range.push(i);
        }
    }

    for (let i of range) {
        if (l) {
            if (i - l === 2) {
                rangeWithDots.push(l + 1);
            } else if (i - l !== 1) {
                rangeWithDots.push('...');
            }
        }
        rangeWithDots.push(i);
        l = i;
    }

    getValidatedPickableProductList(current, item_range, sort, order);

    var pageTable = "<ul class='page-list'>";
    pageTable += "<li onclick='createPagination(" + '"1","' + totalPage + '"' + ");'>&lt;&lt;</li>";

    for (var pageCell of rangeWithDots) {
        if (pageCell != '...') {
            pageTable += '<li onclick="createPagination(' + "'" + pageCell + "','" + totalPage + "'" + ');">' + pageCell + '</li>';
        } else {
            pageTable += '<li>' + pageCell + '</li>';
        }
    }

    pageTable += "</li><li onclick='createPagination(" + '"' + totalPage + '","' + totalPage + '"' + ");'>&gt;&gt;</li>";
    document.getElementById("pagination").innerHTML = pageTable;

    var aTags = document.getElementsByTagName("li");
    var found;

    for (var i = 0; i < aTags.length; i++) {
        if (aTags[i].textContent == parseInt(current)) {
            found = aTags[i];
            found.classList.add("active");
            break;
        }
    }
}

function getValidatedPickableProductList(page, item_range, sort, order) {
    $.ajax({
        url: '/getValidatedPickableProductList?page=' + page + '&item_range=' + item_range + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            if (jsonObject.length > 0) {
                $.each(jsonObject, function (i, object) {
                    var date = new Date(object.create_time).toISOString().replace(/T/, ' ').replace(/\..+/, '')
                    tr += '<tr><td>' +
                        object.product_id_range + '</td><td>' +
                        date + '</td><td>' +
                        object.create_user + '</td><td>' +
                        object.sku + '</td><td>' +
                        object.qty + '</td><td>' + 
                        '<input type="button" class="button regenerate-pdf" value="재고 파악" data-seq="' + object.md_seq_no + '"></tr>';
                });
                $('tbody#pickingHistoryList').html(tr);
                $(".regenerate-pdf").on("click", function (event) {
                    event.preventDefault();
                    $('.loading').css('display', 'table');
                    var seq = $(this).data("seq");
                    var responseBlob = $.ajax({
                        type: 'POST',
                        contentType: "application/json",
                        url: '/regeneratePickableReport?seq_no=' + seq,
                        dataType: "text",
                        responseType: 'blob',
                        success: function (response) {
                            if (!$.trim(response)) {
                                $('.loading').css('display', 'none');
                                alert("No records to be validated.");
                            }
                            else {
                                $('.loading').css('display', 'none');
                                openPDFOnNewTab(response);
                            }
                        }
                    });
                });
            } else {
                var tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
                $('tbody#pickingHistoryList').html(tr);
            }
        },
        async: true,
        error: function () {
            alert("An error has occurred.");
        }
    });
}

function getValidatedListCount() {
    $.ajax({
        url: '/getValidatedListCount',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            count = jsonObject.length;
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / item_range);
            createPagination(current_page, totalPage);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function openPDFOnNewTab(base64EncodedPDF) {
    var byteCharacters = atob(base64EncodedPDF);
    var byteNumbers = new Array(byteCharacters.length);
    for (var i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    var byteArray = new Uint8Array(byteNumbers);
    var file = new Blob([byteArray], { type: 'application/pdf;base64' });
    var fileURL = URL.createObjectURL(file);
    alert("Process Completed!");
    window.open(fileURL);
}