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


    //Get Product ID on Page Load
    var productid_onload = $("#product-dropdown").val();
    $('input[name="saved_product_id"]').val(productid_onload);

    //Get Company ID on Page Load
    var companyid_onload = $("#company-dropdown").val();
    $('input[name="saved_company_id"]').val(companyid_onload);

    // Set main category and sub category to 미정 on page Load
    $('#company-dropdown').val(300);
    $("#manager-dropdown").val(279);

    // Set order type to null if company is Mijeong
    var company_content = $("#company-dropdown").val();
    if (company_content == '300') {
        $('#order_type').val(null);
    }




    // Set manager use flag on page load
    var check_comp_use_flag = $('input[name="comp_use_flag"]');
    var check_mgr_use_flag = $('input[name="mgr_use_flag"]');
    if (check_comp_use_flag.val() == 1) {
        $(check_comp_use_flag).attr("checked", "checked");
    }
    if (check_mgr_use_flag.val() == 1) {
        $(check_mgr_use_flag).attr("checked", "checked");
    }

    // Post form
    $("#create-product").submit(function (event) {
        event.preventDefault();
        //var productid = $('[name="saved_product_id"]').val();
        // Get Product Name from Select dropdown and auto-fill order_product_name
        var get_product_name = $("#product-dropdown").children("option:selected").text()
        var result = get_product_name.substring(1, get_product_name.lastIndexOf("|") + 1);
        var result = get_product_name.split(" | ");
        //console.log(result[1]);
        $('input[name="order_product_name"]').val(result[1]);
        var productName = result[1];
        console.log(productName);

        var selectedProduct = result[0];
        console.log(selectedProduct, "Product id")

        event.preventDefault();
        var cost = parseInt($('[name="unit_price_usd"]').val())
        var regular_price = parseInt($('[name="regular_price"]').val())
        if ($('[name="order_product_name"]').val() == '' || $('[name="unit_price"]').val() == '' || $('[name="carton_qty"]').val() == '' || $('[name="unit_price_usd"]').val() == '' || $('[name="regular_price"]').val() == '' || $('[name="comp_id"]').val() == '') {
            $('[name="order_product_name"]').parent().addClass('input-error');
            $('[name="unit_price"]').parent().addClass('input-error');
            $('[name="carton_qty"]').parent().addClass('input-error');
            $('[name="unit_price_usd"]').parent().addClass('input-error');
            $('[name="comp_id"]').parent().addClass('input-error');
            alert('Please fill in all the highlighted required fields');
            return false;
        }
        else if (cost > regular_price) {
            // $('[name="unit_price_usd"]').parent().addClass('input-error');
            $('.notice').html('<div class="error">단가(달러)가 상품판매가 보다 큽니다.</div>')
            $('.notice .error').fadeIn('fast', function () {
                $('.notice .error').delay(2000).fadeOut();
            });
            return false;

        }
        else {

            var formData = {
                product_id: selectedProduct,
                order_product_name: $('[name="order_product_name"]').val(),
                md_category_id: $('[name="category_id"]').val(),
                grade: $("#grade").val(),
                barcode: $('[name="barcode"]').val(),
                
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
                url: '/createProduct',
                type: 'POST',
                data: JSON.stringify(formData),
                dataType: 'json',
                contentType: 'application/json',
                encode: true,
                success: function (result) {
                    if (result.code == '200') {
                        //window.location='md-edit-product?product_id=' + selectedProduct;
                        console.log("success");

                        // Disable the newly saved product
                        $('#product-dropdown option[value=' + selectedProduct + ']').attr('disabled', 'disabled');
                        $('.notice').html('<div class="success">' + productName + ' has been saved. Select another product.</div>')
                        $('.notice .success').fadeIn('fast', function () {
                            $('.notice .success').delay(3000).fadeOut();
                        });


                    } else {
                        //console.log(result.message);
                        $('.notice').html('<div class="error">An error occured.</div>')
                        $('.notice .error').fadeIn('fast', function () {
                            $('.notice .error').delay(3000).fadeOut();
                        });
                    }
                }
            });
        } //end else
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
            else {
                //console.log(result.message);
                $('.notice').html('<div class="error">An error occured.</div>')
                $('.notice .error').fadeIn('fast', function () {
                    $('.notice .error').delay(2000).fadeOut();
                });
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

                    // Leave these two lines to enable appending of data to inputs
                    $("#company-info").empty();
                    //$("#company-info").append(company.managerList.mgr_name);
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

                    // Mark that manager has NOT been changed
                    $('input[name="mgr_changed"]').val(0);


                    // Set and send order_type (0 = email, 1 = site, 2 = lowest price, 3 = kakaotalk)
                    if (company.comp_type == 0) {
                        $('#order_type').val("0");
                        $('#manager-dropdown').removeAttr('disabled');
                        $('#manager-dropdown').attr('required', 'required');
                    } else if (company.comp_type == 1) {
                        $('#order_type').val("1");
                        $('#manager-dropdown').removeAttr('disabled');
                        $('#manager-dropdown').attr('required', 'required');
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
                    // $('select[name=main_category] option').removeAttr('selected');
                    // $('select[name=main_category] option[value=' + main_category_value +']').attr('selected','selected');
                    // // Set Main Category text on page load
                    // var main_category_text = $("#main_category option:selected").text();
                    // $('#main_category ~ .select2 .select2-selection__rendered').text(main_category_text);


                    // // Set Sub Category selected value on page load
                    // var sub_category_value = $("#sub_category_value").val();
                    // $('select[name=sub_category] option').removeAttr('selected');
                    // $('select[name=sub_category] option[value=' + sub_category_value + ']').attr('selected','selected');
                    // // Set Sub Category text on page load
                    // var sub_category_text = $("#sub_category option:selected").text();
                    // $('#sub_category ~ .select2 .select2-selection__rendered').text(sub_category_text);

                } else {
                    //console.log(result.message);
                    $('.notice').html('<div class="error">An error occured on company dropdown.</div>')
                    $('.notice .error').fadeIn('fast', function () {
                        $('.notice .error').delay(2000).fadeOut();
                    });
                }
            }
        });

    });



    // Change Manager
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
                //console.log(response, 'manager dropdown')
                //console.log(response.code)
                var manager = response.object;
                if (response.code == "200") {
                    //console.log("manager", manager.mgr_id, " was selected");
                    $("input[name='mgr_rank']").val(manager.mgr_rank);
                    $("input[name='mgr_email']").val(manager.mgr_email);
                    $("input[name='mgr_phone_no']").val(manager.mgr_phone_no);

                    if ($('input[name="comp_use_flag"]').val() == 1) {
                        $('input[name="comp_use_flag"]').attr("checked", "checked");
                    }
                    if ($('input[name="mgr_use_flag"]').val() == 1) {
                        $('input[name="mgr_use_flag"]').attr("checked", "checked");
                    }

                    // Mark that manager has been changed
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
                    //console.log("Hidden dropdown triggered");

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

                else if (response.code == "500") {
                    //console.log(result.message);
                    $('.notice').html('<div class="error">An error occured on manager dropdown.</div>')
                    $('.notice .error').fadeIn('fast', function () {
                        $('.notice .error').delay(2000).fadeOut();
                    });
                }

            }
        });
    });

    //Trigger change to Manager dropdown
    if ($("input[name='saved_company_id']").val() == 1) {
        $("#company-dropdown").trigger("change");
        $("#manager-dropdown").trigger("change");
    }


    // Product dropdown
    $("#product-dropdown").change(function () {

        // Get Product Name from Select dropdown and auto-fill order_product_name
        var get_product_name = $("#product-dropdown").children("option:selected").text()
        var result = get_product_name.substring(1, get_product_name.lastIndexOf("|") + 1);
        var result = get_product_name.split(" | ");
        //console.log(result[1]);
        $('input[name="unit_price"]').val("");

        //Clear unit ptice on select
        $('input[name="order_product_name"]').val(result[1]);

        var selectedProduct = result[0];
        var content = {
            product_id: selectedProduct
        };
        console.log(selectedProduct);


        // Get main, sub, hanpoom pic values even when dropdown is changed
        var main_cat_val = $("#main_category").val();
        var sub_cat_val = $("#sub_category").val();
        var hanpoom_pic_val = $("[name='hanpoom_pic']").val();
        console.log(main_cat_val, sub_cat_val, hanpoom_pic_val);



        $.ajax({
            url: '/selectedProduct',
            type: 'POST',
            data: JSON.stringify(content),
            dataType: 'json',
            contentType: 'application/json',
            success: function (response) {
                //console.log(response)
                var product = response.object;
                if (response.code == "200") {
                    //console.log("Triggered product dropdown, Product id is", selectedProduct);
                    $('[name="product_use_flag"]').val(product.product_use_flag),
                        $('[name="main_category"]').val(main_cat_val),
                        $('[name="sub_category"]').val(sub_cat_val),
                        $('[name="hanpoom_pic"]').val(hanpoom_pic_val),
                        $('[name="unit_price_usd"]').val(product.unit_price_usd),
                        $('[name="regular_price"]').val(product.regular_price),
                        $('[name="weight"]').val(product.weight),
                        $('[name="barcode"]').val(product.barcode),
                        $('[name="location"]').val(product.location),
                        $('[name="length"]').val(product.length),
                        $('[name="width"]').val(product.width),
                        $('[name="height"]').val(product.height),
                        $('[name="hs_tariff"]').val(product.hs_tariff),
                        $('[name="goods_desc"]').val(product.goods_desc),
                        $('[name="country_manuf"]').val(product.country_manuf),
                        $('[name="mgr_changed"]').val(product.mgr_changed)
                }
                else {
                    //console.log(result.message);
                    $('.notice').html('<div class="error">An error occured on the product dropdown.</div>')
                    $('.notice .error').fadeIn('fast', function () {
                        $('.notice .error').delay(2000).fadeOut();
                    });
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

    //$("#expList").html(addCategories(myJSONCategories.categories));
    //prepareList();
    //$("#expList > li").addClass("parent");
    //$("#expList > li.parent > input[name='product_cat[]']").prop("disabled", true);

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
                id: id,
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
                    } else {
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
                url: '/removeCategory',
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