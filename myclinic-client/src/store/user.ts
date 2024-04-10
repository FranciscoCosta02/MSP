import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface User {
    username: string | undefined;
    password: string | undefined;
    role: string | undefined;
    token: string | undefined;
}

export type State = User

// Define the initial state using that type
const initialState: State = {
    username: undefined,
    password: undefined,
    role: undefined,
    token: undefined
}

export const slice = createSlice({
    name: 'users',
    initialState,
    reducers: {
        setUser: (state, action: PayloadAction<User>) => {
            state.username = action.payload.username
            state.password = action.payload.password
            state.role = action.payload.role
            state.token = action.payload.token
        },
        deleteUser: (state) => {
            state.username = undefined
            state.password = undefined
            state.role = undefined
            state.token = undefined
        }
    }
})

export const { setUser, deleteUser } = slice.actions

export default slice.reducer