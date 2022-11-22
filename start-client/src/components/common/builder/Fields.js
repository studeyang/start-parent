import PropTypes from 'prop-types'
import get from 'lodash.get'
import React, { useContext } from 'react'

import Actions from './Actions'
import Control from './Control'
import FieldError from './FieldError'
import FieldInput from './FieldInput'
import FieldRadio from './FieldRadio'
import Warnings from './Warnings'
import useWindowsUtils from '../../utils/WindowsUtils'
import { AppContext } from '../../reducer/App'
import { Button, Radio } from '../form'
import { Dependency } from '../dependency'
import { InitializrContext } from '../../reducer/Initializr'

const Fields = ({
  onSubmit,
  onExplore,
  onRun,
  onShare,
  refExplore,
  refSubmit,
  refDependency,
  generating
}) => {
  const windowsUtils = useWindowsUtils()
  const { config, dispatch, dependencies } = useContext(AppContext)
  const { values, dispatch: dispatchInitializr, errors } = useContext(
    InitializrContext
  )
  const update = args => {
    dispatchInitializr({ type: 'UPDATE', payload: args })
  }

  return (
    <>
      <div className='colset colset-main'>
        <div className='left'>
          <Warnings />
          <div className='col-sticky'>
            <div className='colset'>
              <div className='left'>
                <Control text='应用架构'>
                  <Radio
                    name='project'
                    selected={get(values, 'project')}
                    options={get(config, 'lists.project')}
                    onChange={value => {
                      update({ project: value })
                    }}
                  />
                </Control>
              </div>
              <div className='right'>
                <Control text='开发语言'>
                  <Radio
                    name='language'
                    selected={get(values, 'language')}
                    options={get(config, 'lists.language')}
                    onChange={value => {
                      update({ language: value })
                    }}
                  />
                </Control>
              </div>
            </div>

            <Control text='Spring Boot'>
              <Radio
                name='boot'
                selected={get(values, 'boot')}
                error={get(errors, 'boot.value', '')}
                options={get(config, 'lists.boot')}
                onChange={value => {
                  dispatchInitializr({
                    type: 'UPDATE',
                    payload: { boot: value },
                    config: get(dependencies, 'list'),
                  })
                  dispatch({
                    type: 'UPDATE_DEPENDENCIES',
                    payload: { boot: value },
                  })
                }}
              />
              {get(errors, 'boot') && (
                <FieldError>
                  Spring Boot {get(errors, 'boot.value')} is not supported.
                  Please select a valid version.
                </FieldError>
              )}
            </Control>
            <Control text='项目基本信息'>
              <FieldInput
                id='input-group'
                value={get(values, 'meta.group')}
                text='Group'
                onChange={event => {
                  update({ meta: { group: event.target.value } })
                }}
              />
              <FieldInput
                id='input-artifact'
                value={get(values, 'meta.artifact')}
                text='Artifact'
                onChange={event => {
                  update({ meta: { artifact: event.target.value } })
                }}
              />
              <FieldInput
                id='input-name'
                value={get(values, 'meta.name')}
                text='项目名称'
                onChange={event => {
                  update({ meta: { name: event.target.value } })
                }}
              />
              <FieldInput
                id='input-description'
                value={get(values, 'meta.description')}
                text='项目描述'
                onChange={event => {
                  update({ meta: { description: event.target.value } })
                }}
              />
              <FieldInput
                id='input-packageName'
                value={get(values, 'meta.packageName')}
                text='基础包'
                onChange={event => {
                  update({ meta: { packageName: event.target.value } })
                }}
              />
              <FieldRadio
                id='input-packaging'
                value={get(values, 'meta.packaging')}
                text='打包方式'
                options={get(config, 'lists.meta.packaging')}
                onChange={value => {
                  update({ meta: { packaging: value } })
                }}
              />
              <FieldRadio
                id='input-java'
                value={get(values, 'meta.java')}
                text='Java版本'
                options={get(config, 'lists.meta.java')}
                onChange={value => {
                  update({ meta: { java: value } })
                }}
              />
            </Control>
          </div>
        </div>
        <div className='right'>
          <Dependency refButton={refDependency} />
        </div>
      </div>
      <Actions>
        {generating ? (
          <span className='placeholder-button placeholder-button-submit placeholder-button-special'>
            获取中...
          </span>
        ) : (
          <Button
            id='generate-project'
            variant='primary'
            onClick={onSubmit}
            hotkey={`${windowsUtils.symb} + ⏎`}
            refButton={refSubmit}
            disabled={generating}
          >
            获取代码
          </Button>
        )}
        <Button
          id='run-project'
          onClick={onRun}
        >
          创建工程
        </Button>
        <Button
          id='explore-project'
          onClick={onExplore}
          hotkey='Ctrl + Space'
          refButton={refExplore}
        >
          浏览代码
        </Button>
        <Button id='share-project' onClick={onShare}>
          分享...
        </Button>
      </Actions>
    </>
  )
}

Fields.propTypes = {
  generating: PropTypes.bool.isRequired,
  onSubmit: PropTypes.func.isRequired,
  onExplore: PropTypes.func.isRequired,
  onShare: PropTypes.func.isRequired,
  refExplore: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.shape({ current: PropTypes.instanceOf(Element) }),
  ]).isRequired,
  refSubmit: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.shape({ current: PropTypes.instanceOf(Element) }),
  ]).isRequired,
  refDependency: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.shape({ current: PropTypes.instanceOf(Element) }),
  ]).isRequired,
}

export default Fields
