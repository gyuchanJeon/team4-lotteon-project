
window.addEventListener('load', function () {
    const orderLinks = document.getElementsByClassName('orderDetailBtn');
    const orderReviewBtns = document.getElementsByClassName('orderReviewBtn');
    const orderInquireBtns = document.getElementsByClassName('orderInquireBtn');
    const orderSellerBtns = document.getElementsByClassName('orderSellerBtn');
    const orderAcceptBtns = document.getElementsByClassName('orderAcceptBtn');
    const orderReturnBtns = document.getElementsByClassName('orderReturnBtn');
    const orderChangeBtns = document.getElementsByClassName('orderChangeBtn');
    const closeBtns = document.querySelectorAll('.closeBtn');


    for (let i = 0; i < orderLinks.length; i++) {
        orderLinks[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을
            const modal = document.getElementById('productModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < orderReviewBtns.length; i++) {
        orderReviewBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('ReviewModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }



    for (let i = 0; i < orderInquireBtns.length; i++) {
        orderInquireBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('inquireModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < orderSellerBtns.length; i++) {
        orderSellerBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('sellerModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < orderAcceptBtns.length; i++) {
        orderAcceptBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('acceptModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < orderReturnBtns.length; i++) {
        orderReturnBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('ReturnModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < orderChangeBtns.length; i++) {
        orderChangeBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const modal = document.getElementById('ChangeModal');
            if (modal) {
                modal.classList.remove('Modalhidden'); // Modalhidden 클래스 제거
            }
        });
    }

    for (let i = 0; i < closeBtns.length; i++) {
        closeBtns[i].addEventListener('click', function(event) {
            event.preventDefault(); // a 태그의 기본 동작(링크 이동)을 막음
            const displayStatus = event.target.closest('.myInfo_modal');
            if (displayStatus) {
                displayStatus.classList.add('Modalhidden'); // Modalhidden 클래스 추가
            }
        });
    }
});