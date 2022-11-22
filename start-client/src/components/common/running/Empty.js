import '../../../styles/running.scss'

import PropTypes from 'prop-types'

function Empty({ groups, open, onClose }) {
  return (
    <>
      <a
          href='/#'
          onClick={e => {
            e.preventDefault()
            onClose()
          }}
          className='button'
      >
        <span className='button-content' tabIndex='-1'>
          <span>取消</span>
          <span className='secondary desktop-only'>ESC</span>
        </span>
      </a>
    </>
  )
}

Empty.propTypes = {
  groups: PropTypes.array.isRequired,
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
}

export default Empty
