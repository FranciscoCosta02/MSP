import vars from "./var";

function isUserAuthorized(list){
    var role = localStorage.getItem(vars.user_role);
    if(role==null || role===undefined){
        return false;
    }
    return list.includes(role);
}



// eslint-disable-next-line import/no-anonymous-default-export
export default {isUserAuthorized};