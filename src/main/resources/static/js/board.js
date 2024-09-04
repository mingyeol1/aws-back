async function get1() {   // async ->  비동기 처리함수 명시...
    const result = await axios.get(`/board/list`)   // await  -> 비동기 호출
    console.log(result)
    // console.log(result.data)
    return result;
}


// async function get1() {
//     console.log("get1 function called"); // 함수가 호출되는지 확인
//
//     try {
//         const result = await axios.get(`/board/list`);
//         console.log("Result from API:", result); // 통신 결과 로그 출력
//         return result;
//     } catch (error) {
//         console.error("Error during API call:", error); // 에러 로그 출력
//     }
// }
//
// async function getBoardList({bno, page, size, goLast}){
//
//     console.log(`/board/list`);
//     // console.log(bno, page, size)
//     const result = await axios.get(`/board/list`)
//     console.log("HarryPotter: "+result)
//     if(goLast) {
//         const total = result.data.total
//         const lastPage = parseInt(Math.ceil(total/size))
//         return getList({bno:bno, page:lastPage, size:size})
//     }
//     return result.data
// }