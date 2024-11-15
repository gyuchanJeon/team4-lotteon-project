/*
    2024-10-27
    전규찬
    insert 기능 / 일괄등록 편의성 구현
*/

'use strict';

// 일괄 등록 버튼

function inputAllOnClick(button) {
    // 버튼의 data-type 속성 값을 가져옴
    const actionType = button.getAttribute('data-type');

    // 변경할 입력 필드 선택
    const input1 = document.querySelectorAll('.prodOName');
    const input2 = document.querySelectorAll('.prodPrice');
    const input3 = document.querySelectorAll('.prodStock');
    const skuValue = document.querySelector('#inputAll_sku').value;
    const priceValue = document.querySelector('#inputAll_price').value;
    const stockValue = document.querySelector('#inputAll_stock').value;

    // actionType에 따라 다른 동작 수행
    switch (actionType) {
        case 'sku':
            const skuConfirm = confirm(`제품고유명을 ${skuValue}로 일괄 등록하시겠습니까?`);
            if (skuConfirm === true) {
                for (const input of input1) {
                    input.value = skuValue;
                }
                alert('일괄 등록되었습니다.');
            } else {
                alert('취소되었습니다.')
            }
            break;

        case 'price':
            const priceConfirm = confirm(`가격을 ${priceValue}로 일괄 등록하시겠습니까?`);
            if (priceConfirm === true) {
                for (const input of input2) {
                    input.value = priceValue;
                }
                alert('일괄 등록되었습니다.');
            } else {
                alert('취소되었습니다.')
            }
            break;

        case 'stock':

            const stockConfirm = confirm(`수량을 ${stockValue}로 일괄 등록하시겠습니까?`);
            if (stockConfirm === true) {
                for (const input of input3) {
                    input.value = stockValue;
                }
                alert('일괄 등록되었습니다.');
            } else {
                alert('취소되었습니다.')
            }
            break;

        default:
            console.warn('알 수 없는 동작 타입:', actionType);
    }
}

// 모든 버튼에 이벤트 리스너 추가
document.querySelectorAll('.btnInputAll').forEach(button => {
    button.addEventListener('click', function (e) {
        e.preventDefault();
        inputAllOnClick(this);
    });
});

// 옵션 저장은 controller와 service 단에서 함. 순서대로 출력되었기 때문에 나머지 값만 받고 다시 순서대로 넣으면 됨.

// 등록 버튼 이벤트 설정
const btnSubmit = document.querySelector('#product_submit');

btnSubmit.addEventListener('click', function (e) {
    e.preventDefault();

    // 제품고유명, 가격, 수량 값 받아오기
    const prodONameNodes = document.querySelectorAll('.prodOName');
    const prodPriceNodes = document.querySelectorAll('.prodPrice');
    const prodStockNodes = document.querySelectorAll('.prodStock');
    const variantsIdNodes = document.querySelectorAll('.productVariantsId');
    const optionValuesNodes = document.querySelectorAll('.optionValues');
    const optionNamesNodes = document.querySelector('#optionNames');
    const productId = document.querySelector('#productId').value;

    const prodONames = Array.from(prodONameNodes).map(node => node.value.trim());
    const prodPrices = Array.from(prodPriceNodes).map(node => node.value.trim());
    const prodStocks = Array.from(prodStockNodes).map(node => node.value.trim());
    const variantsIds = Array.from(variantsIdNodes).map(node => node.value.trim());
    const optionNames = optionNamesNodes.value;

    // optionValues 의 자식 요소에 속해있는 옵션값들 불러와서 List로 만들기

    const valuesList = [];

    optionValuesNodes.forEach(optionValues => {
        let optionNodes = optionValues.querySelectorAll('.mixedValue');
        let singleValues = Array.from(optionNodes).map(node => node.textContent.trim());
        valuesList.push(singleValues);
    })

    console.log("valuesList = " + valuesList);

    const formData = new FormData();
    formData.append('prodONames', JSON.stringify(prodONames))
    formData.append('prodPrices', JSON.stringify(prodPrices))
    formData.append('prodStocks', JSON.stringify(prodStocks))
    formData.append('variantsIds', JSON.stringify(variantsIds))
    formData.append('valuesList', JSON.stringify(valuesList))
    formData.append('optionNames', JSON.stringify(optionNames))
    formData.append('productId', JSON.stringify(productId))

    fetch('/lotteon/admin/product/more', {
        method: 'PUT',
        body: formData
    })
        .then(resp => resp.json())
        .then(data => {
            console.log(data);
        })
        .catch(err => console.log(err));

    alert('상세 정보 수정이 완료되었습니다! 상품 목록 페이지로 이동합니다.')
    window.location.assign('/lotteon/admin/product/list');

});

