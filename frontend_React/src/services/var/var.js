//User Types
export const u = {
    superUser:"SU",
    staff:"STAFF",
    admin:"ADMIN",
    service:"SERVICE"
};

//URL Paths
export const path={
    home:"/",
    notifications:"/notifications",
    faqs:"/faqs",
    photos:"/photos",
    listUsers:"/list/users",
    stats:"/stats",
    settings:"/settings",
    profile:"/profile",
    groups:"/groups/list",
    toSolve:"/anomalies/toSolve",
    solved:"/anomalies/solved",
    createActivity:"/activity/create",
    listActivities:"/activities/list",
    login:"/login",
    myNotifications:"/user/notifications",
    recoverPWD:"/resetPwd",
    myRooms:"/rooms",
    bugList:"/bugs",
    register:"/register",
}

export const endpoint={
    login:'/login/backOffice',
    logout:'/login',
    recoverPWD:'/recover',
    register:"/register",
}

const vars = {
    bucket_users:"https://storage.googleapis.com/portalnova.appspot.com/users/",
    bucket:"https://storage.googleapis.com/portalnova.appspot.com/",
    app_name:"PORTAL NOVA Back-Office",
    nova_fct:"PORTAL NOVA",
    fct:"FCT",
    roles:"roles",
    user_role:"user_role",
    active:"Active",
    inactive:"Inactive",
    roles_expires:15.0, //15dias
    loginToken_expiration_time:15.0, //15 dias
    listUsersData_expiration:0.005,//7.2min (1440*0.005)
    listUsersData:"_listUsersData",
    user:"user_data",
    loginToken:"loginToken",
    username:"username",
    superUser:u.superUser,//SuperUser
    alerts:{
        alertAutoHideDuration:5000,// 5 segundos
        success:"Success!!",
        error:"An error occurred!",
        auth:{
            login_error:"Error authenticating! Please check your credentials",
            email_success:"Email sent with success!!",
            email_error:"Error sending email!",
            empty_textFields:"Please enter your credentials!",
            no_email:"Please enter your email address",
            new_account_success:"New Account Created with Success",
        },
        activities:{
            date_error:"The end date displays a date before the start date",
            success:"Activity created with success!!",
            error:"Error creating Activity!!"
        },
        notifications:{
            empty:"We can not send an empty message!",
            success:"Notification sent with success!!",
            error:"Error sending notification",
            no_roles:"Error getting roles...",
        },
        groups:{
            error_create:"Error creating group!",
            success_create:"Group created with success!",
            success_delete:"Group deleted with success!",
            error_delete:"Error deleting group!",
        },
        recoverPWD:{
            success:"Success!",
            error:"Code is not valid!",
        },
        bug:{
            success:"Reported with Success!",
            error:"Error reporting bug!",
        },
        rooms:{
            error_delete_booking:"Error canceling booking!",
            success_delete_booking:"Canceled with success!"
        }
    },
    menu:{
        home:[u.admin,u.staff,u.superUser],
        faqs:[u.admin,u.staff,u.superUser,u.service],
        listUsers:[u.admin,u.staff,u.superUser],
        stats:[u.admin,u.superUser,u.service],
        anomalies:[u.admin,u.staff,u.superUser,u.service],
        groups:[u.admin,u.superUser],
        photos:[u.admin,u.superUser,u.staff],
        notifications:[u.admin,u.superUser,u.staff,u.service],
        createActivities:[u.admin,u.superUser,u.staff],
        myrooms:[u.admin,u.superUser,u.staff],
        bugs:[u.superUser,u.service],
        register:[u.superUser,u.admin]
    },
    listUsersAuth:{
        edit:[u.admin,u.superUser],
        delete:[u.superUser],
        editrole:[u.admin,u.superUser],
        activateAccount:[u.admin,u.superUser],
        inactivateAccount:[u.admin,u.superUser]
    },
    anomaliesAuth:{
        solve:[u.admin,u.superUser,u.service],
        unSolve:[u.admin,u.superUser,u.service],
    },
    faqsAuth:{
        create:[u.admin,u.superUser,u.staff,u.service],
        edit:[u.admin,u.superUser,u.staff,u.service],
        remove:[u.admin,u.superUser,u.staff,u.service],
    },
    activities:{
        create:[u.admin,u.superUser,u.staff],
        edit:[u.admin,u.superUser,u.staff],
        remove:[u.admin,u.superUser,u.staff],
        photos:[u.admin,u.superUser,u.staff],
    },
    anomalies:{
        puToSolve:[u.admin,u.superUser,u.service],
    },
    rooms:{
        create:[u.admin,u.superUser,u.staff],
        edit:[u.admin,u.superUser,u.staff],
        remove:[u.admin,u.superUser,u.staff],
        schedule:[u.admin,u.superUser,u.staff],
        reservationsList:[u.admin,u.superUser,u.staff],
    },
    mock:{
        faqTagList:["O Perfil Curricular FCT","Sobre as Inscrições"
        ,"Excesso de Créditos","Inscrição nos Turnos e Horários"
        ,"Sobre os Turnos","Datas","Época Especial","Sobre as Propinas",
        "Sou Caloiro","Erasmus","Questões Gerais","Dúvidas sem Sítio"
        ,"Férias (Trabalhadores)"
        ],
        departments:["DI","Departamental","DCM","DEEC","DF","DM","DCSA","DEMI","DCT","DQ","DCEA","DEC","DCR"],
        departmentsLogin:["DI","DCM","DEEC","DF","DM","DCSA","DEMI","DCT","DQ","DCEA","DEC","DCR","DAT"],
        weekDay:["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]
    }
};


export default vars;