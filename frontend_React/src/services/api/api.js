import axios from 'axios';
import Cookies from 'js-cookie';


const ProjectName= "https://portalnova.oa.r.appspot.com"
const serviceType="/rest"


export const apiLogin = axios.create({
    baseURL: ProjectName+serviceType,
    mode: "cors",
    headers: {'Content-Type': 'application/json'}
})

var token = Cookies.get("loginToken");

export const apiToken = axios.create({
    baseURL: ProjectName+serviceType,
    headers: {'Content-Type': 'application/json','Authorization': 'Bearer ' + token,'Accept':'*/*'}
})

const servletUserPhoto="/gcs"
const bucket="/portalnova.appspot.com";

export const apiUserImage = axios.create({
    baseURL: ProjectName+servletUserPhoto+bucket,
})

const servletActivityPhoto="/act"
export const apiActivityImage = axios.create({
    baseURL: ProjectName+servletActivityPhoto+bucket,
})


