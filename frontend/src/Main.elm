module Main exposing (..)

import Browser
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (..)
import Http
import Json.Decode as Decode
import Json.Encode as Encode

-- MODEL

type alias Model =
    { name : String
    , response : String
    , loading : Bool
    }

init : () -> (Model, Cmd Msg)
init _ =
    ({ name = "", response = "", loading = False }, Cmd.none)

-- UPDATE

type Msg
    = UpdateName String
    | PostHello
    | GetHello
    | GotResponse (Result Http.Error String)

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
    case msg of
        UpdateName name ->
            ({ model | name = name }, Cmd.none)

        PostHello ->
            ({ model | loading = True }, postHelloRequest model.name)

        GetHello ->
            ({ model | loading = True }, getHello)

        GotResponse result ->
            case result of
                Ok message ->
                    ({ model | response = message, loading = False }, Cmd.none)
                
                Err error ->
                    ({ model | response = "Error: " ++ httpErrorString error, loading = False }, Cmd.none)

-- HTTP

postHelloRequest : String -> Cmd Msg
postHelloRequest name =
    Http.post
        { url = "http://localhost:8888/api/hello"
        , body = Http.jsonBody (Encode.object [("name", Encode.string name)])
        , expect = Http.expectJson GotResponse responseDecoder
        }

getHello : Cmd Msg
getHello =
    Http.get
        { url = "http://localhost:8888/api/hello"
        , expect = Http.expectJson GotResponse responseDecoder
        }

responseDecoder : Decode.Decoder String
responseDecoder =
    Decode.field "message" Decode.string

httpErrorString : Http.Error -> String
httpErrorString error =
    case error of
        Http.BadUrl url ->
            "Bad URL: " ++ url
        Http.Timeout ->
            "Request timeout"
        Http.NetworkError ->
            "Network error - check if backend is running on localhost:8888"
        Http.BadStatus status ->
            "Bad status: " ++ String.fromInt status
        Http.BadBody body ->
            "Bad body: " ++ body

-- VIEW

view : Model -> Html Msg
view model =
    div [ style "padding" "20px", style "font-family" "Arial" ]
        [ h1 [] [ text "Hello world using Finatra and Elm" ]
        , div [ style "margin" "10px 0" ]
            [ button [ onClick GetHello, disabled model.loading ] 
                [ text "GET Hello" ]
            ]
        , div [ style "margin" "10px 0" ]
            [ input 
                [ type_ "text"
                , placeholder "Enter your name"
                , value model.name
                , onInput UpdateName
                , style "margin-right" "10px"
                , style "padding" "5px"
                ] []
            , button [ onClick PostHello, disabled (model.loading || String.isEmpty model.name) ] 
                [ text "POST Hello" ]
            ]
        , if model.loading then
            div [ style "color" "blue" ] [ text "Loading..." ]
          else
            div [ style "margin-top" "20px"
            , style "padding" "10px"
            , style "background-color" "#f0f0f0"
            , style "width" "500px" ]
                [ text ("Response: " ++ model.response) ]
        ]

-- MAIN

main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = \_ -> Sub.none
        }