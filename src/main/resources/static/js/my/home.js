'use strict'

const orderLink = document.querySelector('.orderDetailBtn');
const orderReviewBtns = document.getElementsByClassName('orderReviewBtn');
const orderInquireBtns = document.getElementsByClassName('orderInquireBtn');
const orderSellerBtns = document.getElementsByClassName('orderSellerBtn');
const orderAcceptBtns = document.getElementsByClassName('orderAcceptBtn');
const orderReturnBtns = document.getElementsByClassName('orderReturnBtn');
const orderChangeBtns = document.getElementsByClassName('orderChangeBtn');
const closeBtns = document.querySelectorAll('.closeBtn');
const cancelBtns = document.querySelectorAll('.cancelBtn');

orderLink.addEventListener('click', function (event) {
    event.preventDefault(); // a 태그의 기본 동작(링크 이동)을
    const modal = document.getElementById('productModal');
    if (modal) {
        modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
    }
})

// ================================== 상품평 ==================================

// 상품평 작성란에 데이터 주입을 위해 해당 태그 호출
const showVariantId = document.querySelector('#showVariantId');
const showSku = document.querySelector('#showSku');

orderLink.addEventListener('click', function (event) {
    event.preventDefault(); // a 태그의 기본 동작(링크 이동)을
    const modal = document.getElementById('productModal');
    if (modal) {
        modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
    }
})


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

//구매 확정 코드
for (let i = 0; i < orderAcceptBtns.length; i++) {
    orderAcceptBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const orderItemId = event.target.getAttribute('data-order-item-id');
        console.log("전송될 orderItemId:", orderItemId); // 로그로 값 확인
        const modal = document.getElementById('acceptModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
        const confirmButton = modal.querySelector('.confirmAcceptBtn');
        if (confirmButton) {
            confirmButton.addEventListener('click', function () {
                // 서버로 데이터 전송
                fetch('/lotteon/my/home/accept', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ orderItemId: orderItemId })
                })
                    .then(response => {
                        if (response.ok) {
                            alert('구매확정 처리가 완료되었습니다.');
                            modal.classList.add('Modalhidden');
                            window.location.href = '/lotteon/my/home';
                        } else {
                            alert('구매확정 처리에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('에러 발생:', error);
                        alert('에러가 발생했습니다. 다시 시도해주세요.');
                    });
            });
        }
    });
}

//반품 코드
for (let i = 0; i < orderReturnBtns.length; i++) {
    orderReturnBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const orderItemId = event.target.getAttribute('data-order-item-id');
        console.log("전송될 orderItemId:", orderItemId); // 로그로 값 확인
        const modal = document.getElementById('ReturnModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
        const returnButton = modal.querySelector('.returnAcceptBtn');
        if (returnButton) {
            returnButton.addEventListener('click', function () {
                const fileInput = document.getElementById('returnImg');
                if (!fileInput.files || fileInput.files.length === 0) {
                    alert('반품 시 사진이 필수입니다.');
                    return; // 파일이 없으면 서버로 데이터 전송하지 않음
                }

                // 서버로 데이터 전송
                fetch('/lotteon/my/home/return', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ orderItemId: orderItemId })
                })
                    .then(response => {
                        if (response.ok) {
                            alert('반품 처리가 완료되었습니다.');
                            modal.classList.add('Modalhidden');
                            window.location.href = '/lotteon/my/home';
                        } else {
                            alert('반품 처리에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('에러 발생:', error);
                        alert('에러가 발생했습니다. 다시 시도해주세요.');
                    });
            });
        }
    });
}

//교환 코드
for (let i = 0; i < orderChangeBtns.length; i++) {
    orderChangeBtns[i].addEventListener('click', function (event) {
        event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
        const orderItemId = event.target.getAttribute('data-order-item-id');
        console.log("전송될 orderItemId:", orderItemId); // 로그로 값 확인
        const modal = document.getElementById('ChangeModal');
        if (modal) {
            modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
        }
        const changeButton = modal.querySelector('.changeAcceptBtn');
        if (changeButton) {
            changeButton.addEventListener('click', function () {
                // 서버로 데이터 전송
                const fileInput = document.getElementById('changeImg');
                if (!fileInput.files || fileInput.files.length === 0) {
                    alert('교환 시 사진이 필수입니다.');
                    return; // 파일이 없으면 서버로 데이터 전송하지 않음
                }

                // 서버로 데이터 전송
                fetch('/lotteon/my/home/change', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ orderItemId: orderItemId })
                })
                    .then(response => {
                        if (response.ok) {
                            alert('교환 처리가 완료되었습니다.');
                            modal.classList.add('Modalhidden');
                            window.location.href = '/lotteon/my/home';
                        } else {
                            alert('교환 처리에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('에러 발생:', error);
                        alert('에러가 발생했습니다. 다시 시도해주세요.');
                    });
            });
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