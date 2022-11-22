import PropTypes from 'prop-types'
import get from 'lodash.get'
import React, { useContext } from 'react'

import List from './List'
import useWindowsUtils from '../../utils/WindowsUtils'
import { AppContext } from '../../reducer/App'
import { Button } from '../form'
import { InitializrContext } from '../../reducer/Initializr'

function Dependency({ refButton }) {
  const { dispatch, list } = useContext(AppContext)
  const { values } = useContext(InitializrContext)
  const windowsUtils = useWindowsUtils()
  return (
    <div className='control'>
      <div className='dependency-header'>
        <span className='label'>组件依赖</span>
        <Button
          id='explore-dependencies'
          onClick={event => {
            event.preventDefault()
            dispatch({
              type: 'UPDATE',
              payload: { list: !list },
            })
          }}
          hotkey={`${windowsUtils.symb} + b`}
          refButton={refButton}
        >
          添加<span className='desktop-only'>依赖</span>...
        </Button>
      </div>
      {get(values, 'dependencies', []).length > 0 ? (
        <List />
      ) : (
        <div className='no-dependency'>暂未选择任何组件</div>
      )}
    </div>
  )
}

Dependency.propTypes = {
  refButton: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.shape({ current: PropTypes.instanceOf(Element) }),
  ]).isRequired,
}

export default Dependency
