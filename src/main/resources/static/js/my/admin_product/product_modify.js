/*
    2024-10-30 22:29 전규찬
    수정 내역 : 옵션 기존 / 신규 분리, 기존 옵션 삭제 시 알림 추가 / css 보완
*/

'use strict';

// 파일 업로드 파트

const file_inputs = document.querySelectorAll('input[type="file"]');
file_inputs.forEach(input => input.addEventListener('change', function (e) {
    e.preventDefault();
    const files = e.target.files;
    const fileNameSpan = e.target.nextElementSibling.nextElementSibling;
    if (files.length > 0) {
        fileNameSpan.textContent = `현재 선택된 파일 : ${files[0].name}`;
    } else {
        fileNameSpan.textContent = '선택된 파일 없음';
    }
}));


// 상품 옵션 입력 파트 ----------------------------------------------------------------------------

const option = document.querySelector('#option');

const deleteBtns = document.querySelectorAll('.pre-delete-value');

deleteBtns.forEach(btn => btn.addEventListener('click', function (e) {
    e.preventDefault();
    // 부모 div.option-value-box 찾기
    const optionValueBox = this.closest('.pre-option-value-box');
    if (optionValueBox) {
        if (window.confirm("선택하신 옵션을 삭제하면, 이 옵션이 포함된 모든 제품 조합도 함께 삭제됩니다. 삭제를 진행하시겠습니까?")) {

            const optionValue = e.target.previousElementSibling.textContent;
            console.log("optionValue = " + optionValue);


            optionValueBox.remove();
        } else {
            alert('삭제가 취소되었습니다.')
        }
    }
}));

// 옵션값 입력 후 엔터로 추가하는 이벤트
option.addEventListener('keydown', function (event) {

    if (event.target && event.target.classList.contains("option-value-input") && event.key === 'Enter') {
        event.preventDefault(); // 기본 제출 동작 막기

        // 입력 필드가 비어 있지 않은 경우에만 실행
        const optionValueInput = event.target; // 이벤트가 발생한 요소를 직접 참조
        const value = optionValueInput.value.trim();
        let parent = event.target.parentElement;
        let adjacent_name = parent.parentElement.previousElementSibling.querySelector('input').value.trim();

        if (adjacent_name === '') {
            alert('옵션명을 입력해주세요');
            optionValueInput.disabled = true;
            return; // 추가로 진행하지 않도록 함
        }

        if (value !== '') {
            // 옵션값 박스 생성
            const valueBox = document.createElement('div');
            valueBox.classList.add('option-value-box');

            const valueText = document.createElement('span');
            valueText.classList.add('box-value')
            valueText.textContent = value;

            // 삭제 버튼 (X자)
            const deleteButton = document.createElement('span');
            deleteButton.classList.add('delete-value');
            deleteButton.textContent = ' X';

            // 삭제 버튼 클릭 시 옵션값 삭제
            deleteButton.addEventListener('click', function () {
                parent.removeChild(valueBox);
            });

            // 옵션값 박스에 텍스트와 삭제 버튼 추가
            valueBox.appendChild(valueText);
            valueBox.appendChild(deleteButton);
            parent.appendChild(valueBox);

            // 입력창 초기화
            optionValueInput.value = '';
        }
    }
});

// 옵션 추가 버튼
const addOptionButton = document.getElementById('addOption');
if (addOptionButton) {

    addOptionButton.addEventListener('click', function () {

        alert('새로운 옵션이 추가되면, 기존에 있던 모든 제품 조합이 초기화됩니다. 옵션 추가를 진행하시겠습니까?')

        const options = document.querySelector('.option');

        const option_innerHTML = `
        <div>
            <span>옵션명 :</span>
            <input type="text" class="option-name" placeholder="옵션명을 입력하세요" required>
        </div>
        <div class="option-values">
            <span>옵션값 :</span>
            <div class="fake_input">
                <input type="text" class="option-value-input"
                       placeholder="옵션값 입력 후 엔터">
            </div>
        </div>
    `;
        const option_innerHTML2 = `
        <div>
            <span>옵션명 :</span>
            <input type="text" class="option-name" placeholder="옵션명을 입력하세요" required>
        </div>
        <div class="option-values">
            <span>옵션값 :</span>
            <div class="fake_input">
                <input type="text" class="option-value-input"
                       placeholder="옵션값 입력 후 엔터">
            </div>
        </div>
        <span>⚠️ 옵션은 최대 3개까지 가능합니다.</span>
    `;
        const option_name = document.querySelectorAll('.option-name');
        if (option_name.length < 2) {
            option.insertAdjacentHTML("beforeend", option_innerHTML);
        } else if (option_name.length === 2) {
            option.insertAdjacentHTML("beforeend", option_innerHTML2)
            addOptionButton.remove();
        }
    });
}

// 데이터 전송 파트 --------------------------------------------------------------------------------

const form = document.querySelector('form');

form.addEventListener('submit', async function (e) {
    e.preventDefault();

    // 상품 정보
    const productId = document.querySelector('#productId').value.trim();
    const name = document.querySelector('#name').value.trim();
    const description = document.querySelector('#description').value.trim();
    const company = document.querySelector('#company').value.trim();
    const price = document.querySelector('#price').value.trim();
    const discount = document.querySelector('#discount').value.trim();
    const point = document.querySelector('#point').value.trim();
    const deliveryFee = document.querySelector('#deliveryFee').value.trim();
    const sellerId = document.querySelector('#sellerInfoId').value.trim();

    // 이미지 파일 파일명 수정
    const img__1 = document.querySelector('#prod_img1').files;
    const img__2 = document.querySelector('#prod_img2').files;
    const img__3 = document.querySelector('#prod_img3').files;
    const detail__ = document.querySelector('#prod_detail').files;

    const img_1 = img__1[0];
    const img_2 = img__2[0];
    const img_3 = img__3[0];
    const detail_ = detail__[0];

    let img1;
    let img2;
    let img3;
    let detail;

    if (img__1 !== undefined && img__1.length > 0) {
        img1 = document.querySelector('#img1').value;
    }
    if (img__2 !== undefined && img__2.length > 0) {
        img2 = document.querySelector('#img2').value;
    }
    if (img__3 !== undefined && img__3.length > 0) {
        img3 = document.querySelector('#img3').value;
    }
    if (detail__ !== undefined && detail__.length > 0) {
        detail = document.querySelector('#detail').value;
    }

    // 옵션 리스트
    const optionNamesNodeList = document.querySelectorAll('.option-name');
    const fakeInputs = document.querySelectorAll('.fake_input');
    let valueList = [];
    const optionMap = new Map();

    // 옵션 이름을 배열로 변환
    const optionNames = Array.from(optionNamesNodeList).map(node => node.value.trim());

    // valueList 채우기
    fakeInputs.forEach(fakeInput => {
        const boxValues = fakeInput.querySelectorAll('.box-value');
        // box-value의 텍스트 내용을 배열로 저장 (여러 개일 경우)
        const values = Array.from(boxValues).map(box => box.textContent.trim());
        valueList.push(values);
    });

    console.log('Option Names:', optionNames);
    console.log('Value List:', valueList);

    // 길이 확인 및 Map 생성
    const length = Math.min(optionNames.length, valueList.length);
    for (let i = 0; i < length; i++) {
        optionMap.set(optionNames[i], valueList[i]);
    }

    // Map의 내용을 보기 쉽게 출력
    optionMap.forEach((value, key) => {
        console.log(`${key}: ${value}`);
    });

    const optionObj = Object.fromEntries(optionMap);

    const formData = new FormData();
    formData.append('productId', productId);
    formData.append('img_1', img_1);
    formData.append('img_2', img_2);
    formData.append('img_3', img_3);
    formData.append('detail_', detail_);
    formData.append('img1', img1 || '');
    formData.append('img2', img2 || '');
    formData.append('img3', img3 || '');
    formData.append('detail', detail || '');
    formData.append('name', name);
    formData.append('description', description);
    formData.append('company', company);
    formData.append('price', price);
    formData.append('discount', discount);
    formData.append('point', point);
    formData.append('deliveryFee', deliveryFee);
    formData.append('optionsJson', JSON.stringify(optionObj));
    formData.append('sellerId', sellerId);

    console.log("img1 = " + img1);
    console.log("img2 = " + img2);
    console.log("img3 = " + img3);
    console.log("detail = " + detail);

    try {
        // 첫 번째 PUT: 제품 등록
        const registerResponse = await fetch('/lotteon/admin/product', {
            method: 'PUT',
            body: formData
        });

        const registerData = await registerResponse.json();
        console.log('Register Response:', registerData);

        if (registerData.status !== 'success') {
            alert(registerData.message || '기본 정보 등록에 실패하였습니다.');
            return;
        }

        // 두 번째 POST: 제품 상세 등록
        const productDetailId = document.querySelector('#productDetailId').value.trim();
        const condition_field = document.querySelector('#condition').value.trim();
        const duty = document.querySelector('#duty').value.trim();
        const receipt = document.querySelector('#receipt').value.trim();
        const sellerType = document.querySelector('#sellerType').value.trim();
        const brand = document.querySelector('#brand').value.trim();
        const coa = document.querySelector('#coa').value.trim();
        const creator = document.querySelector('#creator').value.trim();
        const country = document.querySelector('#country').value.trim();
        const warning = document.querySelector('#warning').value.trim();
        const createDate = document.querySelector('#createDate').value.trim();
        const quality = document.querySelector('#quality').value.trim();
        const as_field = document.querySelector('#as').value.trim();
        const asPhone = document.querySelector('#asPhone').value.trim();

        const data2 = {
            productDetailId: productDetailId,
            condition_field: condition_field,
            duty: duty,
            receipt: receipt,
            sellerType: sellerType,
            brand: brand,
            coa: coa,
            creator: creator,
            country: country,
            warning: warning,
            createDate: createDate,
            quality: quality,
            as_field: as_field,
            asPhone: asPhone,
        };

        const detailResponse = await fetch('/lotteon/admin/product/detail', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data2)
        });

        const detailData = await detailResponse.json();
        console.log('Detail Response:', detailData);

        if (detailData.status !== 'success') {
            alert(detailData.message || '상품정보 제공고시 등록에 실패하였습니다.');
            return;
        }

        alert('상품 등록이 완료되었습니다. 상품 상세 수정 페이지로 이동합니다.');
        window.location.assign('/lotteon/admin/product/modifyMore?productId=' + productId);

    } catch (err) {
        console.error('Error during product registration:', err);
        alert('상품 등록 중 오류가 발생했습니다.');
    }
});
