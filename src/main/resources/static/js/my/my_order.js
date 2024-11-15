'use strict'

// 기간별 조회 선택한 기간 하이라이트 처리

// 숨겨뒀던 선택된 기간의 값 불러오기
const selected_period = document.querySelector('#selected_period').value;
const selected_month = document.querySelector('#selected_month').value;

// 숨겨놓은 값과 일치하면 해당 태그에 하이라이트용 클래스 명 추가
if (selected_period !== null) {
    switch (selected_period) {
        case "week":
            document.querySelector('#period_week').classList.add('Time_current');
            break;
        case "15days":
            document.querySelector('#period_15days').classList.add('Time_current');
            break;
        case "month":
            document.querySelector('#period_month').classList.add('Time_current');
            break;
    }
}

console.log("selected_month = " + selected_month);

// 숨겨놓은 개월 이름을 포함하는 태그에 하이라이트용 클래스 명 추가 (변환 과정에서 09월과 9월로 나뉘어서 일치하는 조건문은 불가)
if (selected_month !== "") {
    const months = document.querySelectorAll('.months');
    for (const month of months) {
        if (month.textContent.trim().includes(selected_month)) {
            console.log("month.textContent = " + month.textContent)
            month.querySelector('a').classList.add('Time_current');
        }
    }
}

// 사용자 지정 기간 조회 버튼에 이벤트 처리(location href)
const customDate = document.querySelector('#customDate');
customDate.addEventListener('click', function (e) {
    e.preventDefault();
    const inputDates = e.target.previousElementSibling.querySelectorAll('input');
    const date1 = inputDates[0].value;
    const date2 = inputDates[1].value;
    if (date1 && date2) {
        location.href = `/lotteon/my/order?startDate=${encodeURIComponent(date1)}&endDate=${encodeURIComponent(date2)}`;
    } else {
        alert('조회할 기간을 선택해주세요.')
    }
});

const orderAcceptBtns = document.getElementsByClassName('orderAcceptBtn');
const orderReturnBtns = document.getElementsByClassName('orderReturnBtn');
const orderChangeBtns = document.getElementsByClassName('orderChangeBtn');
const orderReviewBtns = document.getElementsByClassName('orderReviewBtn');
const orderSellerBtns = document.getElementsByClassName('orderSellerBtn');
const orderInquireBtns = document.getElementsByClassName('orderInquireBtn');
const closeBtns = document.querySelectorAll('.closeBtn');
const cancelBtns = document.querySelectorAll('.cancelBtn');

// ================================== 상품평 ==================================

// 상품평 작성란에 데이터 주입을 위해 해당 태그 호출
const showVariantId = document.querySelector('#showVariantId');
const showSku = document.querySelector('#showSku');


for (let i = 0; i < orderReviewBtns.length; i++) {
    orderReviewBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음

        showVariantId.value = event.target.parentElement.querySelector('.variantId').value;
        showSku.textContent = event.target.parentElement.querySelector('.sku').value;

        const modal = document.getElementById('ReviewModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

// 현재 선택된 별점을 저장할 변수
let selectedRating = 2;
const starImage = document.getElementById('starImage');
const starRating = document.getElementById('starRating');

// 상품평 별점 클릭 시 변경 기능 추가
document.getElementById('starRating').addEventListener('click', function (event) {
    const starImage = document.getElementById('starImage');
    const ratingInput = document.getElementById('rating');

    // 이미지의 위치와 크기를 가져옴
    const rect = starImage.getBoundingClientRect();
    const x = event.clientX - rect.left; // 이미지 내의 클릭 위치 (가로)
    const width = rect.width;

    const regionWidth = width / 10; // 10개의 영역으로 나눔 (0.5 단위)

    // 클릭된 영역 번호 계산 (1부터 10까지)
    let region = Math.floor(x / regionWidth) + 1;
    if (region === 1) {
        region = 2;
    }

    // 영역 번호를 1과 10 사이로 제한
    region = Math.max(2, Math.min(region, 10));

    // 영역 번호를 별점으로 변환 (0.5 단위)
    const rating = region;

    // 별점 이미지 업데이트
    starImage.src = `../images/review/rating_${rating}.png`;

    // 숨겨진 입력 필드 값 업데이트
    ratingInput.value = rating;

    // 선택된 별점 저장
    selectedRating = rating;

    // ARIA 속성 업데이트
    starRating.setAttribute('aria-valuenow', rating);
});

// 마우스 올렸을 때 별점 미리 보여주는 기능 추가
starRating.addEventListener('mousemove', function (event) {
    const starImage = document.getElementById('starImage');
    const rect = starImage.getBoundingClientRect();
    const x = event.clientX - rect.left; // 이미지 내의 클릭 위치 (가로)
    const width = rect.width;

    const regionWidth = width / 10; // 10개의 영역으로 나눔 (0.5 단위)

    // 클릭된 영역 번호 계산 (1부터 10까지)
    let region = Math.floor(x / regionWidth) + 1;

    // 영역 번호를 1과 10 사이로 제한
    region = Math.max(2, Math.min(region, 10));

    // 영역 번호를 별점으로 변환 (0.5 단위)
    const rating = region;

    // 마우스 오버 시 별점 이미지 미리보기
    starImage.src = `../images/review/rating_${rating}.png`;
});

// 마우스가 떠날 때 선택된 별점 이미지로 복원
starRating.addEventListener('mouseout', function (event) {
    starImage.src = `../images/review/rating_${selectedRating}.png`;
});

const reviewBtn = document.querySelector('#reviewBtn');
reviewBtn.addEventListener('click', function (e) {
    e.preventDefault();

    const variantId = parseInt(showVariantId.value);
    const rating = parseInt(document.querySelector('#rating').value);
    const image1 = document.querySelector('#reviewImage1').files[0];
    const image2 = document.querySelector('#reviewImage2').files[0];
    const content = document.querySelector('#reviewContent').value;

    console.log("variantId = " + variantId);
    console.log("rating = " + rating);
    console.log("image1 = " + image1);
    console.log("image2 = " + image2);
    console.log("content = " + content);

    const formData = new FormData;
    formData.append('variantId', variantId);
    formData.append('rating', rating);
    formData.append('image1', image1);
    formData.append('image2', image2);
    formData.append('content', content);

    fetch('/lotteon/review', {
        method: 'POST',
        body: formData
    })
        .then(resp => {
            if (resp.ok) {
                alert('상품평이 성공적으로 제출되었습니다.');
            } else {
                alert('상품평 제출에 실패하였습니다.')
            }

        })
        .catch(err => console.log(err))

    // 제출 완료 후 모달창 종료
    const displayStatus = event.target.closest('.myInfo_modal');
    if (displayStatus) {
        displayStatus.classList.add('Modalhidden'); // Modalhidden 클래스 추가
    }
});

// ================================== 상품평 끝 ==================================

for (let i = 0; i < orderInquireBtns.length; i++) {
    orderInquireBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const modal = document.getElementById('inquireModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

// 판매자 상세 정보에 데이터 주입을 위해 해당 태그 호출
const sellerGrade = document.querySelector('#sellerGrade');
const sellerComName = document.querySelector('#sellerComName');
const sellerCeo = document.querySelector('#sellerCeo');
const sellerHp = document.querySelector('#sellerHp');
const sellerFax = document.querySelector('#sellerFax');
const sellerEmail = document.querySelector('#sellerEmail');
const sellerComNumber = document.querySelector('#sellerComNumber');
const sellerAddress = document.querySelector('#sellerAddress');

for (let i = 0; i < orderSellerBtns.length; i++) {
    orderSellerBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const modal = document.getElementById('sellerModal');
        sellerGrade.textContent = event.target.parentElement.querySelector('.seller_grade').value;
        sellerComName.textContent = event.target.parentElement.querySelector('.seller_comName').value;
        sellerCeo.textContent = event.target.parentElement.querySelector('.seller_ceo').value;
        sellerHp.textContent = event.target.parentElement.querySelector('.seller_hp').value;
        sellerFax.textContent = event.target.parentElement.querySelector('.seller_fax').value;
        sellerEmail.textContent = event.target.parentElement.querySelector('.seller_email').value;
        sellerComNumber.textContent = event.target.parentElement.querySelector('.seller_comNumber').value;
        sellerAddress.textContent = "[" + event.target.parentElement.querySelector('.seller_zipCode').value.substring(0, 3) + "**] " + event.target.parentElement.querySelector('.seller_addr1').value + event.target.parentElement.querySelector('.seller_addr2').value;
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

for (let i = 0; i < orderAcceptBtns.length; i++) {
    orderAcceptBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const modal = document.getElementById('acceptModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

for (let i = 0; i < orderReturnBtns.length; i++) {
    orderReturnBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const modal = document.getElementById('ReturnModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

for (let i = 0; i < orderChangeBtns.length; i++) {
    orderChangeBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const modal = document.getElementById('ChangeModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
    });
}

for (let i = 0; i < closeBtns.length; i++) {
    closeBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const displayStatus = event.target.closest('.myInfo_modal');
        if (displayStatus) {
            displayStatus.classList.add('Modalhidden'); // Modalhidden 클래스 추가
        }
    });
}

for (let i = 0; i < cancelBtns.length; i++) {
    cancelBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const displayStatus = event.target.closest('.myInfo_modal');
        if (displayStatus) {
            displayStatus.classList.add('Modalhidden'); // Modalhidden 클래스 추가
        }
    });
}