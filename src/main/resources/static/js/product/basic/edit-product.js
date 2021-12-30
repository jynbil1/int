$(document).ready(function () {

    // Disable up, down and minus keys from modifying number inputs
    $("input[type=number]").on("focus", function () {
        $(this).on("keydown", function (event) {
            if (event.keyCode === 38 || event.keyCode === 40 || event.keyCode === 189) {
                event.preventDefault();
            }
        });
    });

    // Select search dropdown
    $(".select-search-single").select2();

    // Compute Volume
    v = 0;
    var l = $('input[name="length"]').val();
    var w = $('input[name="width"]').val();
    var h = $('input[name="height"]').val();
    v = l * w * h;
    $('input[name="volume"]').val(v);

    $('input.compute').on('input', function () {
        v = 0;
        var l = $('input[name="length"]').val();
        var w = $('input[name="width"]').val();
        var h = $('input[name="height"]').val();
        v = l * w * h;
        $('input[name="volume"]').val(v);
    });


    // Set manager use flag on page load
    var check_comp_use_flag = $('input[name="comp_use_flag"]');
    var check_mgr_use_flag = $('input[name="mgr_use_flag"]');
    if (check_comp_use_flag.val() == 1) {
        $(check_comp_use_flag).attr("checked", "checked");
    }
    if (check_mgr_use_flag.val() == 1) {
        $(check_mgr_use_flag).attr("checked", "checked");
    }

    // Set Main Category selected value on page load
    var main_category_value = $("#main_category_value").val();
    $('select[name=main_category] option').removeAttr('selected');
    $('select[name=main_category] option[value=' + main_category_value + ']').attr('selected', 'selected');
    // Set Main Category text on page load
    var main_category_text = $("#main_category option:selected").text();
    $('#main_category ~ .select2 .select2-selection__rendered').text(main_category_text);


    // Set Sub Category selected value on page load
    var sub_category_value = $("#sub_category_value").val();
    $('select[name=sub_category] option').removeAttr('selected');
    $('select[name=sub_category] option[value=' + sub_category_value + ']').attr('selected', 'selected');
    // Set Sub Category text on page load
    var sub_category_text = $("#sub_category option:selected").text();
    $('#sub_category ~ .select2 .select2-selection__rendered').text(sub_category_text);

    var taxable_value = $("#taxable_value").val();
    $('select[name=taxable] option').removeAttr('selected');
    $('select[name=taxable] option[value=' + taxable_value + ']').attr('selected', 'selected');

    var special_value = $("#special_value").val();
    $('select[name=special] option').removeAttr('selected');
    $('select[name=special] option[value=' + special_value + ']').attr('selected', 'selected');

    var grade_value = $("#grade_value").val();
    $('select[name=grade] option').removeAttr('selected');
    $('select[name=grade] option[value=' + grade_value + ']').attr('selected', 'selected');

    var order_rem_status_value = $("#order_rem_status_value").val();
    $('select[name=order_rem_status] option').removeAttr('selected');
    $('select[name=order_rem_status] option[value=' + order_rem_status_value + ']').attr('selected', 'selected');

    var sale_type_value = $("#sale_type_value").val();
    $('select[name=sale_type] option').removeAttr('selected');
    $('select[name=sale_type] option[value=' + sale_type_value + ']').attr('selected', 'selected');

    var freebie_type_value = $("#freebie_type_value").val();
    $('select[name=freebie_type] option').removeAttr('selected');
    $('select[name=freebie_type] option[value=' + freebie_type_value + ']').attr('selected', 'selected');

    // Post form
    $("#edit-product").submit(function (event) {
        event.preventDefault();
        var cost = parseInt($('[name="unit_price_usd"]').val())
        var regular_price = parseInt($('[name="regular_price"]').val())
        if ($('[name="order_product_name"]').val() == '' || $('[name="unit_price"]').val() == '' || $('[name="carton_qty"]').val() == '' || $('[name="moq"]').val() == '' || $('[name="shipping_fee"]').val() == '' || $('[name="unit_bundle"]').val() == '' || $('[name="unit_bundle"]').val() == '' || $('[name="unit_box"]').val() == '') {
            $('[name="order_product_name"]').parent().addClass('input-error');
            $('[name="unit_price"]').parent().addClass('input-error');
            $('[name="carton_qty"]').parent().addClass('input-error');
            $('[name="moq"]').parent().addClass('input-error');
            $('[name="shipping_fee"]').parent().addClass('input-error');
            $('[name="unit_box"]').parent().addClass('input-error');
            $('[name="unit_bundle"]').parent().addClass('input-error');
            alert('Please fill in all the highlighted required fields');
            return false;
        }
        else if (cost > regular_price) {
            $('[name="unit_price_usd"]').parent().addClass('input-error');
            $('[name="regular_price"]').parent().addClass('input-error');
            $('.notice').html('<div class="error">단가(달러)가 상품판매가 보다 큽니다.</div>')
                $('.notice .error').fadeIn('fast', function(){
                    $('.notice .error').delay(2000).fadeOut(); 
                });
            return false;

        }
        else {
            var formData = {
                product_id: $('[name="product_id"]').val(),
                order_product_name: $('[name="order_product_name"]').val(),
                md_category_id: $('[name="category_id"]').val(),
                grade: $("#grade").val(),
                barcode: $('[name="barcode"]').val(),
                SKU: $('[name="sku"]').val(),
                
                unit_price: $('[name="unit_price"]').val(),
                shipping_fee: $('[name="shipping_fee"]').val(),
                unit_bundle: $('[name="unit_bundle"]').val(),
                unit_box: $('[name="unit_box"]').val(),
                carton_qty: $('[name="carton_qty"]').val(),
                moq: $('[name="moq"]').val(),
                order_type: $('[name="order_type"]').val(),
                sale_type: $('[name="sale_type"]').val(),
                freebie_type: $('[name="freebie_type"]').val(),
                is_taxable: $('[name="taxable"]').val(),
                is_special: $('[name="special"]').val(),
                is_exclusive: $('#isExclusive').is(":checked") ? "1" : "0",
                order_rem_status: $("#order_rem_status").val(),
                hanpoom_pic: $('[name="hanpoom_pic"]').val(),
                product_use_flag: $('[name="product_use_flag"]').val(),
                height: $('[name="height"]').val(),
                width: $('[name="width"]').val(),
                length: $('[name="length"]').val(),
                weight: $('[name="weight"]').val(),

                unit_price_usd: $('[name="unit_price_usd"]').val(),
                regular_price: $('[name="regular_price"]').val(),
                location: $('[name="location"]').val(),
                hs_tariff: $('[name="hs_tariff"]').val(),
                goods_desc: $('[name="goods_desc"]').val(),
                country_manuf: $('[name="country_manuf"]').val(),
                
                comp_id: $('[name="comp_id"]').val(),
                comp_name: $('#company-dropdown option:checked').text(),
                mgr_id: $('[name="mgr_id"]').val(),
                mgr_changed: $('[name="mgr_changed"]').val()
                
            };
            console.log(formData);
            $.ajax({
                url: '/editProduct',
                type: 'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success: function (result) {
                    if (result.code == '200') {
                        console.log("Data Updated.");
                        $('.notice').html('<div class="success">Your changes have been saved.</div>')
                        $('.notice .success').fadeIn('fast', function () {
                            $('.notice .success').delay(2000).fadeOut();
                        });

                        // Remove input error
                        $('[name="shipping_fee"]').parent().removeClass('input-error');
                        $('[name="moq"]').parent().removeClass('input-error');

                    } else {
                        console.log(result.message);
                        $('.notice').html('<div class="error">An error occured.</div>')
                        $('.notice .error').fadeIn('fast', function () {
                            $('.notice .error').delay(2000).fadeOut();
                        });
                    }
                }
            });

        }// end validation else
    });


    // Load Company
    var savedCompany = $("#company-dropdown option:selected").val();
    var companyContent = {
        comp_id: savedCompany
    };

    $.ajax({
        url: '/selectedCompany',
        type: 'POST',
        data: JSON.stringify(companyContent),
        dataType: 'json',
        contentType: 'application/json',
        success: function (response) {
            if (response.code == "200") {
                var company = response.object;
                $("input[name='biz_name']").val(company.biz_name);
                $("input[name='biz_no']").val(company.biz_no);
                $("input[name='bank']").val(company.bank);
                $("input[name='account_holder']").val(company.account_holder);
                $("input[name='account']").val(company.account);
                $("input[name='comp_use_flag']").val(company.comp_use_flag);
                $("#reg_image_path").attr("href", company.reg_image_path);

                // Add check to checkbox if company/manager are active
                if ($('input[name="comp_use_flag"]').val() == 1) {
                    $('input[name="comp_use_flag"]').attr("checked", "checked");
                }
                if ($('input[name="mgr_use_flag"]').val() == 1) {
                    $('input[name="mgr_use_flag"]').attr("checked", "checked");
                }

                // Set and send order_type (0 = email, 1 = site, 2 = lowest price, 3 = kakaotalk)
                // If order type is lowest price, disable manager dropdown
                if (company.comp_type == 0) {
                    $('#order_type').val("0");
                } else if (company.comp_type == 1) {
                    $('#order_type').val("1");
                } else if (company.comp_type == 2) {
                    $('#order_type').val("2");
                    $('#manager-dropdown').attr('disabled', 'disabled');
                } else {
                    $('#order_type').val("3");
                }
            }
        }
    });

    // Select Company
    $("#company-dropdown").change(function () {
        var selectedCompany = $(this).val();
        var content = {
            comp_id: selectedCompany
        };
        $.ajax({
            url: '/selectedCompany',
            type: 'POST',
            data: JSON.stringify(content),
            dataType: 'json',
            contentType: 'application/json',
            success: function (response) {
                //console.log(response)
                //console.log(response.code)
                var company = response.object;
                if (response.code == "200") {
                    console.log("company", company.comp_id, " was selected with order_type ", company.comp_type);

                    // Leave these two lines to enable appending of data to inputs
                    $("#company-info").empty();
                    $("#company-info").append(company.managerList.mgr_name);
                    $("input[name='biz_name']").val(company.biz_name);
                    $("input[name='biz_no']").val(company.biz_no);
                    $("input[name='bank']").val(company.bank);
                    $("input[name='account_holder']").val(company.account_holder);
                    $("input[name='account']").val(company.account);
                    $("input[name='comp_use_flag']").val(company.comp_use_flag);
                    $("#reg_image_path").attr("href", company.reg_image_path);


                    // Add check to checkbox if company/manager are active
                    if ($('input[name="comp_use_flag"]').val() == 1) {
                        $('input[name="comp_use_flag"]').attr("checked", "checked");
                    }
                    if ($('input[name="mgr_use_flag"]').val() == 1) {
                        $('input[name="mgr_use_flag"]').attr("checked", "checked");
                    }

                    // List Managers under the Company
                    $('#manager-dropdown').empty();
                    for (var i = 0; i < company.managerList.length; i++) {
                        var option = $("<option value='" + company.managerList[i].mgr_id + "'>" + company.managerList[i].mgr_name + "</option>");
                        $('#manager-dropdown').append(option);
                    }

                    // List Managers under the Company (For Hidden Dropdown)
                    $('#manager-dropdown-hidden').empty();
                    for (var i = 0; i < company.managerList.length; i++) {
                        var option = $("<option value='" + company.managerList[i].mgr_id + "'>" + company.managerList[i].mgr_name + "</option>");
                        $('#manager-dropdown-hidden').append(option);
                    }

                    // Trigger change in hidden manager dropdown so we can display the data from this manager
                    $("#manager-dropdown-hidden").trigger("change");

                    // Even if Company is changed, do not mark Manager Changed value
                    $('input[name="mgr_changed"]').val(0);


                    // Set and send order_type (0 = email, 1 = site, 2 = lowest price, 3 = kakaotalk)
                    // If order type is lowest price, disable manager dropdown
                    if (company.comp_type == 0) {
                        $('#order_type').val("0");
                        $('#manager-dropdown').removeAttr('disabled');
                        $('#manager-dropdown').attr('required', 'required');
                    } else if (company.comp_type == 1) {
                        $('#order_type').val("1");
                        $('#manager-dropdown').removeAttr('disabled');
                    } else if (company.comp_type == 2) {
                        $('#order_type').val("2");
                        $('#manager-dropdown').attr('disabled', 'disabled');
                        $('#manager-dropdown').attr('required', 'required');
                    } else {
                        $('#order_type').val("3");
                        $('#manager-dropdown').removeAttr('disabled');
                        $('#manager-dropdown').attr('required', 'required');
                    }

                    // // Set Main Category selected value on page load
                    // var main_category_value = $("#main_category_value").val();
                    // $('#main_category ~ .select2 .select2-selection__rendered').text(main_category_value);

                    // // Set Sub Category selected value on page load
                    // var sub_category_value = $("#sub_category_value").val();
                    // $('#sub_category ~ .select2 .select2-selection__rendered').text(sub_category_value);


                    // Set Main Category text on page load
                    var main_category_text = $("#main_category option:selected").text();
                    $('#main_category ~ .select2 .select2-selection__rendered').text(main_category_text);

                    // Set Sub Category text on page load
                    var sub_category_text = $("#sub_category option:selected").text();
                    $('#sub_category ~ .select2 .select2-selection__rendered').text(sub_category_text);


                }


            }
        });

    });


    // Load Manager (Display manager details for page load only)
    var savedManager = $("#manager-dropdown option:selected").val();
    var companyManager = {
        mgr_id: savedManager
    };

    $.ajax({
        url: '/selectedManager',
        type: 'POST',
        data: JSON.stringify(companyManager),
        dataType: 'json',
        contentType: 'application/json',
        success: function (response) {
            var manager = response.object;
            if (response.code == "200") {
                console.log("manager", manager.mgr_id, " was selected");
                $("input[name='mgr_rank']").val(manager.mgr_rank);
                $("input[name='mgr_email']").val(manager.mgr_email);
                $("input[name='mgr_phone_no']").val(manager.mgr_phone_no);

                if ($('input[name="comp_use_flag"]').val() == 1) {
                    $('input[name="comp_use_flag"]').attr("checked", "checked");
                }
                if ($('input[name="mgr_use_flag"]').val() == 1) {
                    $('input[name="mgr_use_flag"]').attr("checked", "checked");
                }
            }
        }
    });

    // Change Manager (Shown Dropdown, Used to mark if manager has been changed or not)
    $("#manager-dropdown").change(function () {
        var selectedManager = $(this).val();
        var content = {
            mgr_id: selectedManager
        };

        $.ajax({
            url: '/selectedManager',
            type: 'POST',
            data: JSON.stringify(content),
            dataType: 'json',
            contentType: 'application/json',
            success: function (response) {
                console.log(response)
                //console.log(response.code)
                var manager = response.object;
                if (response.code == "200") {
                    console.log("manager", manager.mgr_id, " was selected");

                    $("input[name='mgr_rank']").val(manager.mgr_rank);
                    $("input[name='mgr_email']").val(manager.mgr_email);
                    $("input[name='mgr_phone_no']").val(manager.mgr_phone_no);

                    if ($('input[name="comp_use_flag"]').val() == 1) {
                        $('input[name="comp_use_flag"]').attr("checked", "checked");
                    }
                    if ($('input[name="mgr_use_flag"]').val() == 1) {
                        $('input[name="mgr_use_flag"]').attr("checked", "checked");
                    }

                    // Mark if manager has been changed
                    $('input[name="mgr_changed"]').val(1);
                }
            }
        });
    });


    // Change Manager (Hidden Dropdown, triggered by Company Dropdown and only used to dispaly contents)
    $("#manager-dropdown-hidden").change(function () {
        var selectedManager = $(this).val();
        var content = {
            mgr_id: selectedManager
        };

        $.ajax({
            url: '/selectedManager',
            type: 'POST',
            data: JSON.stringify(content),
            dataType: 'json',
            contentType: 'application/json',
            success: function (response) {
                console.log(response)
                //console.log(response.code)
                var manager = response.object;
                if (response.code == "200") {
                    console.log("Hidden dropdown triggered");

                    $("input[name='mgr_rank']").val(manager.mgr_rank);
                    $("input[name='mgr_email']").val(manager.mgr_email);
                    $("input[name='mgr_phone_no']").val(manager.mgr_phone_no);

                    if ($('input[name="comp_use_flag"]').val() == 1) {
                        $('input[name="comp_use_flag"]').attr("checked", "checked");
                    }
                    if ($('input[name="mgr_use_flag"]').val() == 1) {
                        $('input[name="mgr_use_flag"]').attr("checked", "checked");
                    }
                }
            }
        });
    });

    var myJSONCategories = {
        "categories": [{
            "id": "001",
            "name": "식품",
            "sub": 1,
            "sub_categories": [{
                "id": "001001",
                "name": "아이들",
                "sub": 1,
                "sub_categories": [{
                    "id": "001001001",
                    "name": "군것질",
                    "sub": 0
                }, {
                    "id": "001001002",
                    "name": "달달이",
                    "sub": 0
                }]
            }, {
                "id": "001002",
                "name": "간식품",
                "sub": 0
            }]
        }, {
            "id": "002",
            "name": "건강",
            "sub": 1,
            "sub_categories": [{
                "id": "002001",
                "name": "간식",
                "sub": 1,
                "sub_categories": [{
                    "id": "002001001",
                    "name": "비타오백",
                    "sub": 0
                }]
            }]
        }]
    };

    function addCategories(obj) {
        htmlBuilder = "";
        for (var i = 0; i < obj.length; i++) {
            htmlBuilder += '<li><input id="' + obj[i].id + '" value="' + obj[i].name + '" data-val="' + obj[i].id + '" type="checkbox" name="product_cat[]">' + '<label>' + obj[i].name + '</label>';

            if (obj[i].sub == 1) {
                htmlBuilder += '<ul class="child">';
                htmlBuilder += addCategories(obj[i].sub_categories);
                htmlBuilder += '</ul>';
            }
            htmlBuilder += '</li>';
        }

        return htmlBuilder;
    }

    // $("#expList").html(addCategories(myJSONCategories.categories));
    prepareList();
    // $("#expList > li").addClass("parent");
    // $("#expList > li.parent > input[name='product_cat[]']").prop("disabled", true);

    $("ul.product-cat-list input[type=checkbox]").on("change", function () {
        $('input[name="product_cat[]"]').not($(this)).prop('checked', false);
        listParentsValue($(this));
        var checkedVals = [];
        checkedVals = $('input[name="product_cat[]"]:checked').map(function () {
            return this.value;
        }).get();
        var checkedIDs = [];
        checkedIDs = $('input[name="product_cat[]"]:checked').map(function () {
            return this.id;
        }).get();
        $("#outputCategory").val(checkedVals.join('/'));
        $("#categoryID").val(checkedIDs.slice(-1)[0]);
        $("#categoryName").val(checkedVals.slice(-1)[0]);
    });

    $('#editCategory').on('click', function () {
        if ($(this).val() == "Edit") {
            $(this).val("Cancel");
            $('.minus-sign, .plus-sign').each(function () {
                $(this).show();
            });
            $('.add-main-div').show();
        }
        else {
            $(this).val("Edit");
            $('.minus-sign, .plus-sign').each(function () {
                $(this).hide();
            });
            $('input[name="product-add-category[]"]').each(function () {
                $(this).closest('ul.child').remove();
            });
            $('.add-main-div').hide()
        }
    });

    $('.plus-sign').on('click', function () {
        var id = $(this).data('id');

        var element = '<ul class="child"><li><div class="input-row"><div class="input-wide"><div class="input-col" style="width: 40%;"><input name="product-add-category[]" data-id="' + id + '" type="text"></div><div class="input-col" style="width: 20%;"><input type="button" class="button primary" value="Add" id="addCategory"></div><div class="input-col" style="width: 20%;"><input type="button" class="button gray" value="Cancel" id="cancelUpdate"></div></div></div></li></ul>';
        $(this).parent().append(element);

        $('.plus-sign').each(function () {
            $(this).hide();
        });

        $('#cancelUpdate').on('click', function () {
            $(this).closest('ul.child').remove();
            $('.plus-sign').each(function () {
                $(this).show();
            });
        });

        $('#addCategory').on('click', function () {
            var id = $('input[name="product-add-category[]"]').data('id');
            var name = $('input[name="product-add-category[]"]').val();

            var formData = {
                parent_id: id,
                name: name
            }

            $.ajax({
                url: '/addCategory',
                type: 'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success: function (result) {
                    if (result.code == '200') {
                        window.location.reload(true);
                    }else if(result.code == '500'){
                        alert(result.message);
                    }else {
                        alert("An error has occurred");
                    }
                },
                error: function () {
                    console.log("error");
                }
            });
        });
    });

    $('.minus-sign').on('click', function () {
        var id = $(this).data('id');
        var name = $(this).data('name');
        var formData = {
            id: id,
            name: name
        }

        if (confirm("Do you want to remove this category?")) {
            $.ajax({
                url: '/deleteCategory',
                type: 'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success: function (result) {
                    if (result.code == '200') {
                        window.location.reload(true);
                    }else if(result.code == '500'){
                        alert(result.message);
                    }else {
                        alert("An error has occurred");
                    }
                },
                error: function () {
                    console.log("error");
                }
            });
        }
    });

    $('.add-main').on('click', function () {
        var element = '<li class="parent"><div class="input-row"><div class="input-wide"><div class="input-col" style="width: 40%;"><input name="product-add-category[]" data-id="" type="text"></div><div class="input-col" style="width: 20%;"><input type="button" class="button primary" value="Add" id="addCategory"></div><div class="input-col" style="width: 20%;"><input type="button" class="button gray" value="Cancel" id="cancelUpdate"></div></div></div></li>';
        $('#expList').append(element);

        $('.plus-sign').each(function () {
            $(this).hide();
        });

        $('#cancelUpdate').on('click', function () {
            $(this).closest('li.parent').remove();
            $('.plus-sign').each(function () {
                $(this).show();
            });
        });
    });

    $('#searchCategory').on('keyup', function () {
        var query = this.value;

        $('input[name="product_cat[]"]').each(function (i, elem) {
            if (elem.value == query.trim()) {
                $('li.parent').css("display", "none");
                $(this).closest('li.parent').css("display", "block");
                $(this).next('label').css("font-weight", "900");
                $(this).next('label').css("color", "#FF3B2C");
            } else if (query == "") {
                $('li.parent').css("display", "block");
                $(this).next('label').css("font-weight", "normal");
                $(this).next('label').css("color", "#3F434A");
            }
        });
    });

    $('#saveCategory').on('click', function () {
        var category = $("#outputCategory").val();

        var id = $('#outputCategory').data('id');
        var name = $('#outputCategory').data('name');
        var formData = {
            id: id,
            name: name
        }

        if (category != "") {
            $.ajax({
                url: '/saveCategory',
                type: 'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success: function (result) {
                    if (result.code == '200') {
                        window.location.reload(true);
                    } else {
                        alert("An error has occurred");
                    }
                },
                error: function () {
                    console.log("error");
                }
            });
        } else {
            alert("Please select a category.");
        }
    });

    var currentCategory = $("#categoryID").val();
    $('input[name="product_cat[]"]').each(function (i, elem) {
        if ($(this).attr("id") == currentCategory) {
            $(this).prop('checked', true);
            listParentsValue($(this));
            var checkedVals = [];
            checkedVals = $('input[name="product_cat[]"]:checked').map(function () {
                return this.value;
            }).get();
            $("#outputCategory").val(checkedVals.join('/'));
            $("#categoryName").val(checkedVals.slice(-1)[0]);
        }
    });
});

function listParentsValue(me) {
    var traverse = false;
    var chkCount = 0;
    var myVal = me.prop("checked");

    $.each($(me).closest(".child").children('li'), function () {
        var checkbox = $(this).children("input[type=checkbox]");
        if ($(checkbox).prop("checked")) {
            chkCount = chkCount + 1;
        }
    });

    if ((myVal == true && chkCount == 1) || (myVal == false && chkCount == 0)) {
        traverse = true;
    }
    if (traverse == true) {
        var inputCheckBox = $(me).closest(".child").siblings("input[type=checkbox]");
        inputCheckBox.prop("checked", me.prop("checked"));
        listParentsValue(inputCheckBox);
    }
}

function prepareList() {
    $('#expList').find('label:has(ul)')
        .click(function (event) {
            if (this == event.target) {
                $(this).toggleClass('expanded');
                $(this).children('ul').toggle('medium');
            }
            return false;
        })
        .addClass('collapsed')
        .children('ul').hide();

    $('#expandList')
        .unbind('click')
        .click(function () {
            $('.collapsed').addClass('expanded');
            $('.collapsed').children().show('medium');
        })
    $('#collapseList')
        .unbind('click')
        .click(function () {
            $('.collapsed').removeClass('expanded');
            $('.collapsed').children().hide('medium');
        })
}