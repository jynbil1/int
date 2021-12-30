//global variables
var item_range, current_page, keyword, count, totalPage, sort, search_by, order;

$(document).ready(function () {
    item_range = $("#item_range").val();
    current_page = $("#listPage").val();
    keyword = $(".search > input").val().trim();
    search_by = $("#selectRange").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    getPaginatedPickableProductListCount(keyword, search_by,);

    $("#searchBtn").on("click", function (event) {
        event.preventDefault();
        keyword = $(".search > input").val().trim();
        current_page = 1;
        $("#listPage").val(current_page);
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage);
        getPaginatedPickableProductListCount(keyword, search_by);
    });

    $('.search > input[type="text"]').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#searchBtn').click();
        }
    });

    $('#item_range').change(function () {
        keyword = $(".search > input").val().trim();
        current_page = 1;
        $("#listPage").val(current_page);
        item_range = $(this).val();
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage);
        getPaginatedPickableProductListCount(keyword, search_by);
    });
    
    $("#inventoryIdentification").on("click", function (event) {
        event.preventDefault();
        $('.loading').css('display', 'table');
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/generatePickableReport',
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
            },
            error: function () {
                $('.loading').css('display', 'none');
                alert("An error has occurred.");
            }
        });
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

    getPagedPickableProductList(current, item_range, keyword, search_by, sort, order);

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

function getPagedPickableProductList(page, item_range, keyword, search_by, sort, order) {
    $.ajax({
        url: '/getPagedPickableProductList?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&search_by=' + search_by + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            if (jsonObject.length > 0) {
                $.each(jsonObject, function (i, object) {
                    var status = object.order_status == "wc-processing" ? "Processing" : object.order_status == "wc-preparing" ? "Preparing" : "";
                    tr += '<tr><td>' +
                        object.order_no + '</td><td>' +
                        object.order_date + '</td><td>' +
                        status + '</td><td>' +
                        object.customer_name + '</td><td>' +
                        object.order_amount + '</td></tr>';
                });
                $('tbody#pickableProductsList').html(tr);
                $("#inventoryIdentification").prop('disabled', false);
            } else {
                var tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
                $('tbody#pickableProductsList').html(tr);
            }
        },
        async: true,
        error: function () {
            alert("An error has occurred.");
        }
    });
}

function getPaginatedPickableProductListCount(keyword, search_by) {
    $.ajax({
        url: '/getPaginatedPickableProductListCount?keyword=' + keyword + '&search_by=' + search_by,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            $.each(jsonObject, function (i, object) {
                count = object.total_count;
                $("#totalProducts").text(count);
                totalPage = Math.ceil(count / item_range);
                createPagination(current_page, totalPage);
            });
        },
        async: true,
        error: function () {
            alert("error");
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
    window.open(fileURL);
    window.location.reload(true);
}