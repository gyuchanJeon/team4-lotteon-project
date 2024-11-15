'use strict'

// 현재 month 에 스타일 차별점을 주기 위해 Time_current 클래스 추가
const months = document.querySelectorAll('.months');
const months_length = months.length;

if (months_length > 0) {
    const lastMonth = months[months_length - 1];
    console.log(lastMonth);
    lastMonth.querySelector('a').classList.add('Time_current');
} else {
    console.log('No elements with class "months" found.');
}
