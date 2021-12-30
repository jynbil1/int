async function getShipmentStatus(carrier, trackingNo, url) {
    let resultSet = {};
    switch (carrier) {
        case "CJ":
            resultSet = getCJShipment(carrier, trackingNo, url)
            break;
        case "천일":
            resultSet = getChunilShipment(carrier, trackingNo, url)
            break;
        case "CU":
            resultSet = getCUShipment(carrier, trackingNo, url)
            break;
        case "대신":
            resultSet = getDeshinShipment(carrier, trackingNo, url)
            break;
        case "합동":
            resultSet = getHapdongShipment(carrier, trackingNo, url)
            break;
        case "일양":
            resultSet = getIlyangShipment(carrier, trackingNo, url)
            break;
        case "건영":
            resultSet = getKunyungShipment(carrier, trackingNo, url)
            break;
        case "경동":
            resultSet = getKyungdongShipment(carrier, trackingNo, url)
            break;
        case "로젠":
            resultSet = getLogenShipment(carrier, trackingNo, url)
            break;
        case "농협":
            resultSet = getNonghyupShipment(carrier, trackingNo, url)
            break;
        case "우체국":
            resultSet = getPostShipment(carrier, trackingNo, url)
            break;
        case "우리":
            resultSet = getWooriShipment(carrier, trackingNo, url)
            break;
        case "롯데":
            resultSet = getLotteShipment(carrier, trackingNo, url)
            break;
        case "한진":
            resultSet = getHanjinShipment(carrier, trackingNo, url)
            break;
    }
}


function getCJShipment(trackingNo) {

}

function getChunilShipment(trackingNo) {

}

function getCUShipment(trackingNo) {

}

function getDeshinShipment(trackingNo) {

}

function getHapdongShipment(trackingNo) {

}

function getIlyangShipment(trackingNo) {

}

function getKunyungShipment(trackingNo) {

}

function getKyungdongShipment(trackingNo) {

}

function getLogenShipment(trackingNo) {

}

function getNonghyupShipment(trackingNo) {

}

function getPostShipment(trackingNo) {

}

function getWooriShipment(trackingNo) {

}

function getLotteShipment(trackingNo) {

}

function getHanjinShipment(trackingNo) {

}