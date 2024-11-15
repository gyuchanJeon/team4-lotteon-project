'use strict';

const checkAll = document.querySelector('#checkAll');

checkAll.addEventListener('click', function () {

    const isChecked = checkAll.checked;

    if (isChecked) {
        const checkboxes = document.querySelectorAll('.chk');

        document.querySelector('#delete-btn').removeAttribute('disabled');

        for (const checkbox of checkboxes) {
            checkbox.checked = true;
        }
    } else {
        const checkboxes = document.querySelectorAll('.chk');

        document.querySelector('#delete-btn').setAttribute('disabled', 'true');

        for (const checkbox of checkboxes) {
            checkbox.checked = false;
        }
    }
})

const checkboxes = document.querySelectorAll('.chk');

for (const checkbox of checkboxes) {
    checkbox.addEventListener('click', function () {

        const totalCnt = checkboxes.length;

        const checkedCnt = document.querySelectorAll('.chk:checked').length;

        if (totalCnt === checkedCnt) {
            document.querySelector('#checkAll').checked = true;
        } else {
            document.querySelector('#checkAll').checked = false;
        }

        if (checkedCnt > 0) {
            document.querySelector('#delete-btn').removeAttribute('disabled');
        } else if (checkedCnt === 0) {
            document.querySelector('#delete-btn').setAttribute('disabled', 'true');
        }

    });
}

const delete_btns = document.querySelectorAll('.deletebtn');
for (const delete_btn of delete_btns) {
    delete_btn.addEventListener('click', function (e) {
        e.preventDefault();

        if (confirm('해당 상품을 삭제하시겠습니까?')) {

            const productId = delete_btn.value;
            console.log("prodId = " + productId);

            fetch(`/lotteon/admin/product/${productId}`, {
                method: 'DELETE'
            })
                .then(resp => resp.json())
                .then(data => {
                    console.log("data.status =" + data.status);
                    if (data.status === "success") {
                        alert('해당 상품이 삭제되었습니다.');
                        console.log("부모부모요소 = " + e.target.parentElement.parentElement)
                        e.target.parentElement.parentElement.remove();
                    } else {
                        alert('해당 상품 삭제가 실패하였습니다.')
                    }
                })
                .catch();

        } else {
            alert('삭제가 취소되었습니다.')
        }
    });
}


const delete_selected = document.querySelector('#delete-btn');

delete_selected.addEventListener('click', function (e) {
    e.preventDefault();
    const checkedAll = document.querySelectorAll('.chk:checked');
    const productIds = Array.from(checkedAll).map(node => node.value.trim());

    if (confirm('선택하신 상품을 삭제하시겠습니까?')) {


        console.log(productIds);

        const formData = new FormData;
        formData.append('productIds', JSON.stringify(productIds));

        fetch('/lotteon/admin/product', {
            method: 'DELETE',
            body: formData
        })
            .then(resp => resp.json())
            .then(data => {
                if (data.status === "success") {
                    console.log("부모부모요소 = " + e.target.parentElement.parentElement)
                    for (const checked of checkedAll) {
                        checked.parentElement.parentElement.remove();
                    }
                    alert('선택하신 상품이 삭제되었습니다.');
                } else {
                    alert('해당 상품 삭제가 실패하였습니다.')
                }
            })
    } else{
        alert('삭제가 취소되었습니다')
    }
});




