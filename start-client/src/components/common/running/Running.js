import React, {useContext, useEffect, useState} from 'react'
import {toast} from 'react-toastify'
import {Form, Modal, Tooltip} from "@douyinfe/semi-ui"
import {postRun, getProjectParams, httpGet} from '../../utils/ApiUtils'
import useWindowsUtils from '../../utils/WindowsUtils'
import {InitializrContext} from "../../reducer/Initializr"
import get from "lodash.get"
import {AppContext} from "../../reducer/App"

function Running() {

  const windowsUtils = useWindowsUtils()
  const [formValue, setFormValue] = useState(null)
  const [loading, setLoading] = useState(null)
  const {
    share,
    values
  } = useContext(InitializrContext)
  const {
    dispatch,
    runningForm,
    gitlabGroups,
    gitlabBranches,
    dependencies
  } = useContext(AppContext)
  const createProjectRequest = {}

  const init = async () => {
    const getGroupsUrl = `${windowsUtils.origin}/gitlab/groups?run=true&` + share
    const getGroupsResponse = await httpGet(getGroupsUrl).catch(() => {
      toast.error(`Could not connect to server. Please check your network.`)
    })
    if (getGroupsResponse.action === 'REDIRECT') {
      window.location.href = getGroupsResponse.data
    } else if (getGroupsResponse.action === 'DATA') {
      const getBranchesUrl = `${windowsUtils.origin}/gitlab/1944/branches`
      const getBranchesResponse = await httpGet(getBranchesUrl).catch(() => {
        toast.error(`Could not connect to server. Please check your network.`)
      })
      dispatch({
        type: 'UPDATE',
        payload: {
          gitlabGroups: getGroupsResponse.data,
          gitlabBranches: getBranchesResponse.data
        }
      })
    }
  }

  // get groups and branches
  useEffect(() => {
    if (runningForm && gitlabGroups.length === 0) {
      init().then(() => console.log('init running data.'))
    }
  }, [runningForm, gitlabGroups])

  const onClose = () => {
    dispatch({
      type: 'UPDATE',
      payload: { list: false, share: false, explore: false, nav: false, runningForm: false },
    })
  }

  const onSubmit = () => {
    const url = `${windowsUtils.origin}/gitlab/project`
    formValue.validate().then(async (form) => {
      const foundGroup = gitlabGroups.find(group =>
          group.id === form.groupId
      )

      createProjectRequest.namespaceId = form.groupId
      createProjectRequest.groupPath = foundGroup.path
      createProjectRequest.branch = form.branchName
      createProjectRequest.path = get(values, 'meta.artifact')
      createProjectRequest.projectName = get(values, 'meta.artifact')
      createProjectRequest.webProjectRequest = getProjectParams(values, get(dependencies, 'list'))

      setLoading(true)
      const response = await postRun(
        url,
        JSON.stringify(createProjectRequest)
      ).catch(() => {
        toast.error(`Could not connect to server. Please check your network.`)
      })

      if (response.action === 'SUCCESS') {
        const sshUrl = response.data.ssh_url_to_repo;
        await navigator.clipboard.writeText(sshUrl)
        toast.success('已复制至粘贴板：' + sshUrl)

        setTimeout(() => {
          onClose()
        }, 1000)
      } else if (response.action === 'FAIL') {
        toast.error(response.data)
      }
      setLoading(false)
    })
  }

  const getFormApi = (formApi) => {
    setFormValue(formApi)
  }

  const tipStyle = {
    fontSize: '8px'
  }

  return (
    <>
      <Modal
        title="创建工程"
        visible={runningForm}
        onOk={onSubmit}
        style={{ width: 600 }}
        onCancel={onClose}
        confirmLoading={loading}
      >
        <Form
          getFormApi={getFormApi}
          labelPosition='left'
          labelWidth='150px'
          labelAlign='right'
          style={{ padding: '10px', width: 530 }}
        >
          <Form.Select
            label={<Tooltip content="工程将创建至该分组" style={tipStyle}>Gitlab Groups</Tooltip>}
            placeholder='请选择Gitlab分组'
            field='groupId'
            style={{ width: 360 }}
          >
            {gitlabGroups.map(group =>
              <Form.Select.Option value={group.id}>{group.name}</Form.Select.Option>
            )}
          </Form.Select>
          <Form.Select
            label={<Tooltip content="CICD配置的分支" style={tipStyle}>icec-devops 分支</Tooltip>}
            placeholder='请选择CICD所属分支'
            field='branchName'
            style={{ width: 360 }}
          >
            {gitlabBranches.map(branch =>
              <Form.Select.Option value={branch.name}>{branch.name}</Form.Select.Option>
            )}
          </Form.Select>
        </Form>
      </Modal>
    </>
  )
}

export default Running
