import React from 'react'
import { useUserSelector } from '../../store/hooks'
import { store } from '../../store'
import { deleteUser, setUser, User } from '../../store/user'
import { Link } from 'react-router-dom'
import './login.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft} from "@fortawesome/free-solid-svg-icons";
//import { deleteReservations } from '../../store/reservations'

export const Navigation = () => {
    const role = useUserSelector(state => state.role)

    return <div>
        {role === "ROLE_CLIENT" && (
            <div>
                <Link to='/apartments/page/1' style={{marginRight: '10px'}}>All Apartments</Link>|
                <Link to='/reservations' style={{marginLeft: '10px'}}>My Reservations</Link>
            </div>
        )}

        {role === "ROLE_OWNER" && (
            <div>
                <Link to='/apartments/page/1' style={{marginRight: '10px'}}>My Apartments</Link>
            </div>
        )}

        {role === "ROLE_MANAGER" && (
            <div>
                <Link to='/apartments/page/1' style={{marginRight: '10px'}}>All Apartments</Link>
            </div>
        )}

        {role === "" || role === undefined && (
            <div>
                <Link to='/apartments/page/1' style={{marginRight: '10px'}}>All Apartments</Link>
            </div>
        )}

    </div>
}
/*
export const Login = () => {

    let username = useUserSelector(state => state.username)
    let password = useUserSelector(state => state.password)

    const handleUsername = (event: any) => {
        username = event.target.value
    }

    const handlePassword = (event: any) => {
        password = event.target.value
    }

    const execLogout = () => {
        store.dispatch(deleteUser())
        //store.dispatch(deleteReservations())
    }

    const execLogin = () => {

        console.log(username)
        console.log(password)
        fetch('/login', {
            method: 'POST',
            headers: {
                'accept': 'application/json',
                'content-type': 'application/json',
            },
            body: JSON.stringify({
                'username': username,
                'password': password
            })
        })
            .then(response => response.headers)
            .then(headers => {
                const user: User = {
                    username: username,
                    password: password,
                    role: headers.get('Authentication-Info')!,
                    token: headers.get('Authorization')!
                }

                store.dispatch(setUser(user))
            })

    }

    const logout = <div>Hello {username} <button onClick={execLogout}>Logout</button></div>

    const login =
        <div>
            username: <input type="text" onChange={handleUsername} />
            password: <input type="text" onChange={handlePassword}/>
            <button onClick={execLogin}>Sign in</button>
        </div>

    return username ? logout : login

     */
export const Login = () => {

    const login =
        <div className={"form-container"}>
            <p className={"title"}>Login</p>
            <form className={"form"}>
                <input type="email" className={"input"} placeholder="Email"/>
                <input type="password" className={"input"} placeholder="Password"/>
                <Link to="/">
                    <button className={"form-btn"}>Enter</button>
                </Link>
            </form>

            <p className={"sign-up-label"}>
                Don't have an account? <Link to="/create-account" className={"sign-up-link"}>Create account</Link>
            </p>

        </div>

    return login
}

export const CreateAccount = () => {

    const create =
        <div className={"create-container"}>
            <p className={"title"}>Create Account</p>
            <form className={"form"}>
                <input type="name" className={"input"} placeholder="Name"/>
                <input type="email" className={"input"} placeholder="Email"/>
                <input type="password" className={"input"} placeholder="Password"/>
                <Link to="/">
                    <button className={"form-btn"}>Enter</button>
                </Link>
            </form>

            <p className={"sign-up-label"}>
                Already have an account? <Link to="/login" className={"sign-up-link"}>Login</Link>
            </p>


        </div>

    return create
}