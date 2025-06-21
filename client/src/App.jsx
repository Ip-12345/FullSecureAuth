import { ToastContainer } from 'react-toastify'
import './App.css'
import Home from './pages/Home'
import EmailVerify from './pages/EmailVerify'
import Login from './pages/Login'
import ResetPassword from './pages/ResetPassword'
import { Route, Routes } from 'react-router-dom'

function App() {
  return (
    <div>
      <ToastContainer/>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/email-verify' element={<EmailVerify/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/reset-password' element={<ResetPassword/>}/>
      </Routes>
    </div>
  )
}

export default App
