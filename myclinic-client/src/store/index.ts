import { configureStore } from '@reduxjs/toolkit';
import { logger } from 'redux-logger'
import userReducer from './user';

export const store = configureStore({
    reducer: {
        //user: userReducer,
        //apartments: apartmentsReducer,
        //reservations: reservationsReducer
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat([logger]),
});

export type State = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

//store.dispatch(loadApartments())