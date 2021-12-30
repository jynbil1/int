$(document).ready(function () {

    var intable = $('#testtable').DataTable({});

    $('.selectedBtn').on('click', function (event) {
        $('.itemRows').removeClass('selected');
        $(event.target).closest(".itemRows").addClass("selected");
    });

    $('.selectedBtn').click(function () {
        var selectRowData = intable.rows('.selected').data();

        $('#order_no').val(selectRowData[0][0]);
        $('#order_date').val(selectRowData[0][2]);
        console.log(selectRowData[0][0]);
        console.log(selectRowData[0][2]);
        $('#sendpost').attr("action", "/packing/generate-packing-slip");
        $('#sendpost').attr("method", "post");
        $('#sendpost').attr("target", "pdfpage" + selectRowData[0][0]);
        window.open("", "pdfpage" + selectRowData[0][0], "");
        $('#sendpost').submit();
    });
});
