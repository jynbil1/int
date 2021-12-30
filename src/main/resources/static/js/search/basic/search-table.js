$(document).ready(function() {

    var searchtable = $('#search-table').DataTable({
        "lengthMenu": [
            [10, 25, 50, -1],
            [10, 25, 50, "All"]
        ]
    });

    $('#search-table tbody').on('click', 'tr', function() {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            searchtable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });


    $(window).on("load", function() {
        $(".loader-wrapper").fadeOut("slow");
    });

});