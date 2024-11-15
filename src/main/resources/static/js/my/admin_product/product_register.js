'use strict';

// 카테고리 출력 파트 ------------------------------------------------------------------------------
const category1 = document.querySelector('#category1');
const category2 = document.querySelector('#category2');
const category3 = document.querySelector('#category3');

category1.addEventListener('change', function (e) {
    const parent = e.target.value;

    fetch(`/lotteon/admin/product/${parent}`)
        .then(resp => resp.json())
        .then(data => {
            category2.innerHTML = "<option value=\"\" disabled selected hidden>2차분류 선택</option>";
            category3.innerHTML = "<option value=\"\" disabled selected hidden>3차분류 선택</option>";

            data.forEach(option2 => {
                const option = document.createElement('option');
                option.value = option2.productCateId;
                option.text = option2.name;
                category2.appendChild(option);
            });
        })
        .catch(err => console.log(err));
});

category2.addEventListener('change', function (e) {
    const parent = e.target.value;

    fetch(`/lotteon/admin/product/${parent}`)
        .then(resp => resp.json())
        .then(data => {
            category3.innerHTML = "<option value=\"\" disabled selected hidden>3차분류 선택</option>";
            if (data !== undefined) {
                data.forEach(option3 => {
                    const option = document.createElement('option');
                    option.value = option3.productCateId;
                    option.text = option3.name;
                    category3.appendChild(option);
                });
            }
        })
        .catch(err => console.log(err));
});

// 상품 옵션 입력 파트 ----------------------------------------------------------------------------

const option = document.querySelector('#option');

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
addOptionButton.addEventListener('click', function () {
    const option = document.querySelector('#option');

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

// 데이터 전송 파트 --------------------------------------------------------------------------------

const form = document.querySelector('form');

form.addEventListener('submit', async function (e) {
    e.preventDefault();

    const cateId2 = category2.value;
    const cateId3 = category3.value;

    let cateId;

    if (!cateId3) {
        cateId = cateId2;
    } else {
        cateId = cateId3;
    }

    console.log('Selected Category ID:', cateId);

    // 상품 정보
    const name = document.querySelector('#name').value.trim();
    const description = document.querySelector('#description').value.trim();
    const company = document.querySelector('#company').value.trim();
    const price = document.querySelector('#price').value.trim();
    const discount = document.querySelector('#discount').value.trim();
    const point = document.querySelector('#point').value.trim();
    const deliveryFee = document.querySelector('#deliveryFee').value.trim();
    const sellerId = document.querySelector('#sellerInfoId').value.trim();

    // 이미지 파일 파일명 수정
    const img_1 = document.querySelector('#prod_img1').files[0];
    const img_2 = document.querySelector('#prod_img2').files[0];
    const img_3 = document.querySelector('#prod_img3').files[0];
    const detail_ = document.querySelector('#prod_detail').files[0];

    const fileInputs = document.querySelectorAll('input[name="files"]');

    // 모든 파일 입력 요소에서 선택된 파일을 수집
    const files = [];

    fileInputs.forEach(input => {
        if (input.files.length > 0) {
            // 각 입력 요소에서 선택된 모든 파일을 배열에 추가
            Array.from(input.files).forEach(file => {
                files.push(file);
            });
        }
    });

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
    formData.append('cateId', cateId);
    formData.append('img_1', img_1 || '');
    formData.append('img_2', img_2 || '');
    formData.append('img_3', img_3 || '');
    formData.append('detail_', detail_ || '');
    formData.append('name', name);
    formData.append('description', description);
    formData.append('company', company);
    formData.append('price', price);
    formData.append('discount', discount);
    formData.append('point', point);
    formData.append('deliveryFee', deliveryFee);
    formData.append('optionsJson', JSON.stringify(optionObj));
    formData.append('sellerId', sellerId);

    try {
        // 첫 번째 POST: 제품 상세 등록
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
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data2)
        });

        const detailData = await detailResponse.json();
        console.log('Detail Response:', detailData);

        const productDetailId = detailData.productDetailId;
        if (!productDetailId) {
            alert('유효한 상품 상세 ID를 받지 못했습니다.');
            return;
        }

        formData.append('product_Detail_Id', productDetailId);

        // 두 번째 POST: 제품 등록
        const registerResponse = await fetch('/lotteon/admin/product', {
            method: 'POST',
            body: formData
        });

        const registerData = await registerResponse.json();
        console.log('Register Response:', registerData);

        const productId = registerData.productId;
        if (!productId) {
            alert('유효한 상품 ID를 받지 못했습니다.');
            return;
        }

        alert('상품 등록이 완료되었습니다. 상품 상세 등록 페이지로 이동합니다.');
        window.location.assign('/lotteon/admin/product/registerMore?productId=' + productId);

    } catch (err) {
        console.error('Error during product registration:', err);
        alert('상품 등록 중 오류가 발생했습니다.');
    }
});
