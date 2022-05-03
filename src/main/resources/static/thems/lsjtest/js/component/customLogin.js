const Form = Next.Form;
const FormItem = Form.Item;
const Input = Next.Input;
const Box = Next.Box;
const Card = Next.Card;
const Button = Next.Button;

const formItemLayout = {
    labelCol: {fixedSpan: 3},
    wrapperCol: {span: 24}
};

class Demo extends React.Component {
    constructor(...args) {
        super(...args);

        this.state = {
            code: "",
            second: 60
        };

        this.handleSubmit = (values, errors) => {
            if (errors) {
                return;
            }
            console.log("Get form value:", values);
            $('#custom-login-form').attr('action', "lsjtest");
        };

        this.sendCode = (values, errors) => {
            if (errors) {
                return;
            }
            this.setState({
                code: Math.floor(Math.random() * (999999 - 99999 + 1) + 99999)
            });

            setInterval(() => {
                this.setState({
                    second: --this.state.second
                });
            }, 1000);
        };
    }

    onSubmit(e, errors) {
        console.log("onsubmit");
    }

    getKey() {
        return document.getElementById("execution-key").value;
    }

    render() {
        const {code} = this.state;
        return (
            <Form
                id="custom-login-form"
                style={{width: 400}}
                {...formItemLayout}
                labelTextAlign="left"
                size="large"
                labelAlign="inset"
                action={"lsjtest"}
                onSubmit={this.onSubmit.bind(this)}
                method="post"
            >
                <input type="hidden" name="execution" value={this.getKey()}/>
                <FormItem name="username" label="用户名" required asterisk={false}>
                    <Input name="username" trim/>
                </FormItem>
                <FormItem
                    name="password"
                    label="密码"
                    required
                    asterisk={false}
                >
                    <Input name="password" trim/>
                </FormItem>
                <FormItem label=" ">
                    <Form.Submit
                        htmlType="submit"
                        style={{width: "100%"}}
                        type="primary"
                        validate
                        onClick={this.handleSubmit}
                    >
                        登录
                    </Form.Submit>
                </FormItem>
            </Form>
        );
    }
}

const loginBox =
    <Box direction="row" justify="center" padding={20}>
        <Box className="box-450-300" align="center" style={{border: 0, borderRight: ['1px solid #ddd']}}>
            {/*<Box className="box-400-250" align="center" justify="center" style={{border: 0}}>*/}
            {/*    <div id="qrcode"/>*/}
            {/*</Box>*/}
            <Card free style={{width: 300}} hasBorder={false}>
                <Card.Content align="center">
                    <Card free style={{width: 234}}>
                        <Card.Content align="center">
                            <div id="qrcode"/>
                        </Card.Content>
                    </Card>
                </Card.Content>
                <Card.Divider/>
                <Card.Content align="center">
                    请扫码登录
                </Card.Content>
            </Card>
        </Box>
        <Box className="box-450-300" align="center" style={{border: 0, borderLeft: ['1px solid #ddd']}}>
            <Box className="box-400-250" justify="center" style={{border: 0}}><Demo/></Box>
        </Box>
    </Box>;

ReactDOM.render(loginBox, document.getElementById('lsjtest'));

let qrCode = new QRCode(document.getElementById("qrcode"), {
    text: "https://baidu.com",
    width: 200,
    height: 200,
    colorDark: "#22ddbb",
    colorLight: "#ffffff",
    correctLevel: QRCode.CorrectLevel.H
});