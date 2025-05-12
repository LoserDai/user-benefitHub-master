const base = {
    get() {
        return {
            url : "http://localhost:8080/user-benefitHub-master/",
            name: "user-benefitHub-master",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/user-benefitHub-master/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "音乐产品购物网站"
        } 
    }
}
export default base
