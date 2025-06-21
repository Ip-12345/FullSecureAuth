import React from 'react'
import Menubar from '../components/Menubar'
import Header from '../components/Header'

function Home() {
  return (
    <div className='flex flex-col items-center justify-center-between min-vh-100'>
      <Menubar/>
      <Header/>
    </div>
  )
}

export default Home
