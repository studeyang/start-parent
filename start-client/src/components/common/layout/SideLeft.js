import get from 'lodash.get'
import React, { useContext, useEffect, useRef, useState } from 'react'
import { CSSTransition, TransitionGroup } from 'react-transition-group'
import { clearAllBodyScrollLocks, disableBodyScroll } from 'body-scroll-lock'

import Header from './Header'
import { AppContext } from '../../reducer/App'
import { IconGithub, IconTwitter } from '../icons'

const SideLeft = () => {
  const [isOpen, setIsOpen] = useState(false)
  const [lock, setLock] = useState(false)
  const wrapper = useRef(null)

  const { nav, dispatch } = useContext(AppContext)

  useEffect(() => {
    if (get(wrapper, 'current') && nav) {
      disableBodyScroll(get(wrapper, 'current'))
    }
    return () => {
      clearAllBodyScrollLocks()
    }
  }, [wrapper, isOpen, nav])

  const onEnter = () => {
    setLock(true)
    setTimeout(() => {
      setIsOpen(true)
    }, 350)
  }
  const onEntered = () => {
    setLock(false)
  }

  const onEnded = () => {
    setLock(true)
    setIsOpen(false)
  }
  const onExited = () => {
    setLock(false)
  }
  return (
    <>
      <div id='side-left' className={'is-open'}>
        <div className='side-container'>
        </div>
      </div>
    </>
  )
}

export default SideLeft
