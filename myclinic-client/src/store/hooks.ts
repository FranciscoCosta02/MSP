import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux'
import type {State as UserState} from './user'
import {State, AppDispatch} from "./index";


// Typed versions of useDispatch and useSelector hooks
export const useAppDispatch: () => AppDispatch = useDispatch
export const useAppSelector: TypedUseSelectorHook<State> = useSelector

export const useUserSelector: TypedUseSelectorHook<UserState> =
    <T>(f:(state:UserState) => T) => useAppSelector((state:State) => f(state.user))